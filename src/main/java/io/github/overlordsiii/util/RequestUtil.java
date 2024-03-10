package io.github.overlordsiii.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.overlordsiii.api.Article;
import io.github.overlordsiii.request.Request;
import io.github.overlordsiii.request.Requests;
import io.github.overlordsiii.stockblogger.StockBlogger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.*;

public class RequestUtil {

    public static JsonObject chatGPTHasError(JsonObject response) {
        if (response.has("error")) {
            return response.get("error").getAsJsonObject();
        }

        return null;
    }

    public static String[] getRivals(JsonObject response) {
        JsonArray choices = response.getAsJsonArray("choices");
        JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");

        String content = message.get("content").getAsString();

        System.out.println("Content of ChatGPT Rivals Request:\n" + content);

        String[] unedited = content.split("\n");
        String[] rivals = new String[unedited.length];

        for (int i = 0; i < unedited.length; i++) {
            List<String> company = new ArrayList<>(Arrays.asList(unedited[i].split("\\s+")));
            company.remove(0);
            List<String> company2 = new ArrayList<>();
            for (String s : company) {
                if (s.startsWith("(")) {
                    break;
                }
                company2.add(s);
            }

            String rival = String.join(" ", company2);
            rivals[i] = rival;
        }

        return rivals;
    }

    public static Double getPrice(JsonObject object) {
        if (JsonUtils.validResponse("price", object)) {

            return Double.parseDouble(object.get("price").getAsString());
        } else {
            return null;
        }
    }

    public static String getName(String symbol) throws IOException, InterruptedException {
        Request request = Requests.makeStockNameRequest(symbol);

        JsonObject response = request.makeRequest();

        if (!response.has("data")) {
            System.out.println("Error when querying!");
            System.out.println("Response:\n" + JsonUtils.elementToString(response));
            return null;
        }

        JsonArray array = response.getAsJsonArray("data");

        if (array.isEmpty()) {
            return symbol;
        }

        JsonObject object = array.get(0).getAsJsonObject();

        return object.get("name").getAsString();
    }

    public static Double getPrice(String symbol) throws IOException, InterruptedException {
        Request request = Requests.makeStockPriceRequest(symbol);

        JsonObject object = request.makeRequest();

        Double price = RequestUtil.getPrice(object);

        if (price == null) {
            System.out.println("Error when finding price for symbol: \n" + symbol);
            System.out.println("Response: \n" + JsonUtils.elementToString(object));
        }

        return price;
    }

    public static Map<String, Double> getRivalPrices(String[] rivals) throws IOException, InterruptedException {
        Map<String, Double> map = new HashMap<>();

        for (String rival : rivals) {
            String symbol = getStockSymbol(rival);

            if (symbol == null) {
                continue;
            }

            Double price = getPrice(symbol);

            map.put(symbol, price);
        }

        return map;
    }

    // returns stock data with
    // first element stock price right now
    // next ones are stock prices one month seperated
    // goes back 3 years
    public static List<Double> getAllHistoricalStockData(String symbol) throws IOException, InterruptedException {
        List<Double> doubles = new ArrayList<>();

        Request request = Requests.makeHistoricalPriceRequest(symbol);

        JsonObject response = request.makeRequest();

        if (!response.has("values")) {
            System.out.println("Error when querying historical stock data!");
            System.out.println("Response:\n" + JsonUtils.elementToString(response));
            return null;
        }

        JsonArray array = response.getAsJsonArray("values");

        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();
            doubles.add(Double.parseDouble(object.get("close").getAsString()));
        }

        return doubles;
    }
    public static String getStockSymbol(String bestGuessName) throws IOException, InterruptedException {
        JsonObject object = Requests.makeSymbolSearchRequest(bestGuessName).makeRequest();

        if (!object.has("data")) {
            throw new RuntimeException("Stock Symbol Request didn't Work! This means something very bad happened!");
        }

        JsonArray array = object.get("data").getAsJsonArray();
        int i = 0;
        for (JsonElement element : array) {
            JsonObject object1 = element.getAsJsonObject();
            String name = object1.get("instrument_name").getAsString();

            System.out.println(i + " - " + name + " (" + object1.get("country").getAsString() + " - " + object1.get("exchange").getAsString() +  ")");
            i++;
        }

        System.out.println("Enter the number off the stock you desire (if you know the stock symbol and it's not here, enter it instead). If you don't see any companies listed, it is likely your requested company is not public on the stock market. Please type \" + null + \" if that is so. ");

        String line = StockBlogger.SCANNER.nextLine().trim();

        if (MiscUtil.isNum(line)) {
            int num = Integer.parseInt(line);
            return array.get(num).getAsJsonObject().get("symbol").getAsString();
        }

        if (line.equalsIgnoreCase("null")) {
            return null;
        }

        return line.toUpperCase();
    }

    //ensures all spaces are replace
    public static String urlifyString(String string) {
        return string.replaceAll("\\s+", "");
    }

    public static String getStockAPIKey() {
        Random random = new Random();
        boolean bl = random.nextBoolean();

        return StockBlogger.API_KEY.getConfigOption(bl ? "twelveDataApiKey" : "twelveDataApiKey2");
    }

    public static String getChatGptApiKey() {
        Random random = new Random();
        boolean bl = random.nextBoolean();

        return StockBlogger.API_KEY.getConfigOption(bl ? "chatGptApiKey" : "chatGptApiKey2");
    }

    public static List<Article> getArticles(String symbol) throws IOException, InterruptedException, URISyntaxException {
        List<Article> articles = new ArrayList<>();

        JsonArray array = Requests.makeNewsRequestEODHD(symbol).makeRequestToArray();

        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();

            String title = object.get("title").getAsString();
            URL url = new URL(object.get("link").getAsString());
            String desc = object.get("content").getAsString();
            String date = object.get("date").getAsString();
            LocalDateTime time = LocalDateTime.parse(date.substring(0, date.indexOf("+")));

            Article article = new Article(title, url.toURI(), desc, time);



            String htmlContent = getHtmlContent(object.get("link").getAsString());
            article.setHtmlContent(htmlContent);

            List<String> bps = getBulletPoints(htmlContent, symbol);

            article.setBulletPoints(bps);

            articles.add(article);
        }

        return articles;
    }

    public static List<String> getBulletPoints(String html, String symbol) throws IOException, InterruptedException {
        Document doc = Jsoup.parse(html);

        doc.select("script").remove();
        doc.select("footer").remove();
        doc.select("header").remove();
        doc.select("head").remove();
        doc.select("meta").remove();
        doc.select("link").remove();

        html = doc.outerHtml();

        Request req = Requests.requestChatGPTSummary(html, symbol);

        JsonObject response = req.makeRequest();

        if (!response.has("choices")) {
            System.out.println("Error making request to Chat GPT!");
            System.out.println(JsonUtils.elementToString(response));
            return new ArrayList<>();
        }

        JsonArray array = response.getAsJsonArray("choices");
        String rawString = array.get(0).getAsJsonObject().get("message").getAsJsonObject().get("content").getAsString();

        return new ArrayList<>(Arrays.asList(rawString.split("\n")));
    }

    public static String getHtmlContent(String url) throws IOException {
        System.out.println("Made GET request to " + url);
        URL urlObj = new URL(url);
        URLConnection connection = urlObj.openConnection();
        InputStream stream = connection.getInputStream();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8")))
        {
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(inputLine);
            }

            return stringBuilder.toString();
        }
    }


    public static String getLogoUrl(String symbol) {
        return "https://eodhd.com/img/logos/US/" + symbol + ".png";

    }
}