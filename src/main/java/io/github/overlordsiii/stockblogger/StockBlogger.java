package io.github.overlordsiii.stockblogger;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.overlordsiii.stockblogger.api.Article;
import io.github.overlordsiii.stockblogger.api.Stock;
import io.github.overlordsiii.stockblogger.gui.StockBloggerGUI;
import io.github.overlordsiii.stockblogger.request.Request;
import io.github.overlordsiii.stockblogger.request.Requests;
import io.github.overlordsiii.stockblogger.config.PropertiesHandler;
import io.github.overlordsiii.stockblogger.util.JsonUtils;
import io.github.overlordsiii.stockblogger.util.MiscUtil;
import io.github.overlordsiii.stockblogger.util.RequestUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class StockBlogger {
    public static final PropertiesHandler API_KEY = PropertiesHandler
            .builder()
            .addConfigOption("twelveDataApiKey", "")
            .addConfigOption("chatGptApiKey", "")
            .addConfigOption("twelveDataApiKey2", "") // for rate limits, we alternate api keys each request
            .addConfigOption("eodhdApiKey", "")
            .setFileName("api_keys.properties")
            .requireNonNull()
            .build();

    public static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        processStock();
    }

    public static void processStock() throws IOException, InterruptedException, URISyntaxException {
        // TODO Encapsulate / Abstractify more of the functions in this big main method

        Stock selectedStock = requestUserStock();

        List<Stock> rivalStocks = requestUserRivalStocks(selectedStock);

        List<Article> articles = RequestUtil.getArticles(selectedStock.getSymbol());

        debug(selectedStock, rivalStocks, articles);

        StockBloggerGUI.createGui(selectedStock, rivalStocks, articles);
    }

    public static List<Stock> requestUserRivalStocks(Stock selectedStock) throws IOException, InterruptedException {
        System.out.println("Executing Chat-GPT Request");

        Request chatgpt = Requests.requestChatGPTRivals(selectedStock.getName());

        JsonObject response = chatgpt.makeRequest();

        JsonObject error = RequestUtil.chatGPTHasError(response);

        if (error != null) {
            if (error.get("message").getAsString().contains("exceeded your current quota")) {
                System.out.println("Chat GPT API Key has exceeded it's current quota! Please update the Chat GPT API Key in api_keys.properties!");
            } else {
                System.out.println("Error Occurred!");
                System.out.println("Error: \n" + JsonUtils.elementToString(error));
            }

            throw new RuntimeException();
        }


        List<String> rivals = RequestUtil.getRivals(response);

        List<Stock> rivalStocks = new ArrayList<>();

        System.out.println("Please answer the following questions: ");
        Map<String, Double> map = RequestUtil.getRivalPrices(rivals);

        for (Map.Entry<String, Double> e : map.entrySet()) {
            String key = e.getKey();
            Double value = e.getValue();

            if (key == null || value == null) {
                continue;
            }

            rivalStocks.add(new Stock(key, RequestUtil.getName(key), value, RequestUtil.getLogoUrl(key.toUpperCase())));
        }

        List<Double> doubles = RequestUtil.getAllHistoricalStockData(selectedStock.getSymbol());

        Objects.requireNonNull(doubles).forEach(selectedStock::addHistoricalDataPoint);

        for (Stock rivalStock : rivalStocks) {
            Objects.requireNonNull(RequestUtil.getAllHistoricalStockData(rivalStock.getSymbol()))
                .forEach(rivalStock::addHistoricalDataPoint);
        }

        return rivalStocks;
    }

    public static Stock requestUserStock() throws IOException, InterruptedException {
        System.out.println("What stock do you want to analyze?");

        String stock = SCANNER.nextLine();

        String symbol = RequestUtil.getStockSymbol(stock);

        if (symbol == null) {
            throw new NullPointerException("Make sure you choose a public stock!");
        }

        Double price = RequestUtil.getPrice(symbol);

        if (price == null) {
            throw new RuntimeException("Error when parsing response!");
        }

        return new Stock(symbol, RequestUtil.getName(symbol), price, RequestUtil.getLogoUrl(symbol.toUpperCase()));
    }

    public static void debug(Stock selectedStock, List<Stock> rivalStocks, List<Article> articles) throws IOException {
        StringBuilder builder = new StringBuilder();

        builder.append("======================================\n");
        builder.append("Here is your stock data analysis!:\n");
        builder.append("Your selected stock: " + selectedStock.getName() + "\n");
        builder.append("Price: " + selectedStock.getPrice() + "\n");
        builder.append("Now here are your rivals:\n");
        rivalStocks.forEach(stock1 -> {
            builder.append(stock1.getName() + " - " + stock1.getPrice() + "\n");
        });

        builder.append("Here is your historical stock data: \n");
        builder.append(selectedStock.getName() + " - " + MiscUtil.getFormattedMap(selectedStock.getHistoricalData(), String::valueOf, aDouble -> new JsonPrimitive("$" + aDouble)) + "\n");
        builder.append("Now here is the rival historical stock data: \n");

        rivalStocks.forEach(stock1 -> {
            builder.append(stock1.getName() + " - " + MiscUtil.getFormattedMap(stock1.getHistoricalData(), String::valueOf, aDouble -> new JsonPrimitive("$" + aDouble)) + "\n");
        });

        articles.forEach(article -> {
            builder.append(article.getTitle() + "(" + article.getUrl() + ")\n");
            article.getSummarizedBulletPoints().forEach(s -> {
                builder.append(s + "\n");
            });
        });

        builder.append("======================================\n");

        System.out.println(builder);

        JsonUtils.createJsonTestFile(selectedStock, rivalStocks, articles);
    }
}
