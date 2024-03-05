package io.github.overlordsiii.stockblogger;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.overlordsiii.api.Stock;
import io.github.overlordsiii.request.Request;
import io.github.overlordsiii.request.Requests;
import io.github.overlordsiii.stockblogger.config.PropertiesHandler;
import io.github.overlordsiii.util.JsonUtils;
import io.github.overlordsiii.util.MiscUtil;
import io.github.overlordsiii.util.RequestUtil;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class StockBlogger {
    public static final PropertiesHandler API_KEY = PropertiesHandler
            .builder()
            .addConfigOption("twelveDataApiKey", "")
            .addConfigOption("chatGptApiKey", "")
            .addConfigOption("twelveDataApiKey2", "") // for rate limits, we alternate api keys each request
            .addConfigOption("marketauxApiKey", "")
            .setFileName("api_keys.properties")
            .requireNonNull()
            .build();

    public static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {
        API_KEY.validateNonNull();

        // TODO Encapsulate / Abstractify more of the functions in this big main method
        System.out.println("What stock do you want to analyze?");

        String stock = SCANNER.nextLine();

        String symbol = RequestUtil.getStockSymbol(stock);

        Double price = RequestUtil.getPrice(symbol);

        if (price == null) {
            System.out.println("Error when parsing response!");
            return;
        }

        Stock selectedStock = new Stock(stock, RequestUtil.getName(symbol), price);

        System.out.println("Executing Chat-GPT Request");

        Request chatgpt = Requests.requestChatGPTRivals(stock);

        JsonObject response = chatgpt.makeRequest();

        JsonObject error = RequestUtil.chatGPTHasError(response);

        if (error != null) {
            if (error.get("message").getAsString().contains("exceeded your current quota")) {
                System.out.println("Chat GPT API Key has exceeded it's current quota! Please update the Chat GPT API Key in api_keys.properties!");
            } else {
                System.out.println("Error Occurred!");
                System.out.println("Error: \n" + JsonUtils.objToString(error));
            }

            return;
        }


        String[] rivals = RequestUtil.getRivals(response);

        List<Stock> rivalStocks = new ArrayList<>();

        System.out.println("Please answer the following questions: ");
        Map<String, Double> map = RequestUtil.getRivalPrices(rivals);

        for (Map.Entry<String, Double> e : map.entrySet()) {
            String key = e.getKey();
            Double value = e.getValue();
            rivalStocks.add(new Stock(key, RequestUtil.getName(key), value));
        }

        List<Double> doubles = RequestUtil.getAllHistoricalStockData(symbol);

        Objects.requireNonNull(doubles).forEach(selectedStock::addHistoricalDataPoint);

        for (Stock rivalStock : rivalStocks) {
            Objects.requireNonNull(RequestUtil.getAllHistoricalStockData(rivalStock.getSymbol()))
                    .forEach(rivalStock::addHistoricalDataPoint);
        }

        List<Article> articles = RequestUtil.getArticles(symbol);

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
        builder.append(selectedStock.getName() + " - " + MiscUtil.getFormattedMap(selectedStock.getHistoricalData(), integer -> integer + " Months Ago", aDouble -> new JsonPrimitive("$" + aDouble)) + "\n");
        builder.append("Now here is the rival historical stock data: \n");

        rivalStocks.forEach(stock1 -> {
            builder.append(stock1.getName() + " - " + MiscUtil.getFormattedMap(stock1.getHistoricalData(), integer -> integer + " Months Ago", aDouble -> new JsonPrimitive("$" + aDouble)) + "\n");
        });

        articles.forEach(article -> {
            builder.append(article.getTitle() + "(" + article.getUrl() + ")\n");
        });

        builder.append("======================================\n");

        System.out.println(builder);
    }
}
