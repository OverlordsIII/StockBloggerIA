package io.github.overlordsiii.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.overlordsiii.request.Request;
import io.github.overlordsiii.request.Requests;
import io.github.overlordsiii.stockblogger.StockBlogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

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
            System.out.println("Response:\n" + JsonUtils.objToString(response));
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
            System.out.println("Response: \n" + JsonUtils.objToString(object));
        }

        return price;
    }

    public static Map<String, Double> getRivalPrices(String[] rivals) throws IOException, InterruptedException {
        Map<String, Double> map = new HashMap<>();

        for (String rival : rivals) {
            String symbol = getStockSymbol(rival);

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
            System.out.println("Response:\n" + JsonUtils.objToString(response));
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

        System.out.println("Enter the number off the stock you desire (if you know the stock symbol and it's not here, enter -1)");

        int num = StockBlogger.SCANNER.nextInt();

        if (num == -1) {
            System.out.println("What is the stock symbol instead for " + bestGuessName + "?");

            String line = StockBlogger.SCANNER.nextLine();

            return line;
        }

        return array.get(num).getAsJsonObject().get("symbol").getAsString();
    }

    //ensures all spaces are replace
    public static String urlifyString(String string) {
        return string.replaceAll("\\s+", "");
    }

    public static String getStockAPIKey() {

    }
}
