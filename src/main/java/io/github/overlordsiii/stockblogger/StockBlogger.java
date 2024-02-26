package io.github.overlordsiii.stockblogger;

import com.google.gson.JsonObject;
import io.github.overlordsiii.request.Request;
import io.github.overlordsiii.request.Requests;
import io.github.overlordsiii.stockblogger.config.PropertiesHandler;
import io.github.overlordsiii.util.JsonUtils;
import io.github.overlordsiii.util.RequestUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

public class StockBlogger {
    public static final PropertiesHandler API_KEY = PropertiesHandler
            .builder()
            .addConfigOption("stockAPIKey", "")
            .addConfigOption("chatGptApiKey", "")
            .setFileName("api_keys.properties")
            .requireNonNull()
            .build();

    public static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO Encapsulate / Abstractify more of the functions in this big main method
        System.out.println("What stock do you want to analyze?");

        String stock = SCANNER.nextLine();

        String symbol = RequestUtil.getStockSymbol(stock);

        Double price = RequestUtil.getPrice(symbol);

        if (price == null) {
            System.out.println("Error when parsing response!");
            return;
        }

        // builder.append()("Price: " + price);

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

        //  builder.append()("Rivals: ");

        for (String rival : rivals) {
            //   builder.append()(rival);
        }

        System.out.println("Please answer the following questions: ");
        Map<String, Double> map = RequestUtil.getRivalPrices(rivals);

        System.out.println("These are the popular rivals to " + stock + " and their respective stock prices: ");
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            String s = entry.getKey();
            Double aDouble = entry.getValue();
            //  builder.append()(RequestUtil.getName(s) + ": " + aDouble);
        }

        List<Double> doubles = RequestUtil.getAllHistoricalStockData(symbol);

        Map<String, List<Double>> rivalsHistoricalData = new HashMap<>();

        for (Map.Entry<String, Double> entry : map.entrySet()) {
            String s = entry.getKey();
            rivalsHistoricalData.put(s, RequestUtil.getAllHistoricalStockData(s));
        }

        StringBuilder builder = new StringBuilder();

        builder.append("======================================\n");
        builder.append("Here is your stock data analysis!:\n");
        builder.append("Your selected stock: " + RequestUtil.getName(symbol) + "\n");
        builder.append("Price: " + price + "\n");
        builder.append("Now here are your rivals:\n");
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            String s = entry.getKey();
            Double aDouble = entry.getValue();
            builder.append(RequestUtil.getName(s) + " - " + aDouble + "\n");
        }

        builder.append("Here is your historical stock data: \n");
        builder.append(RequestUtil.getName(symbol) + " - " + doubles + "\n");
        builder.append("Now here is the rival historical stock data: \n");
        for (Map.Entry<String, List<Double>> entry : rivalsHistoricalData.entrySet()) {
            String s = entry.getKey();
            List<Double> doubles1 = entry.getValue();
            builder.append(RequestUtil.getName(s) + " - " + doubles1 + "\n");
        }
        builder.append("======================================\n");

        System.out.println(builder);
    }
}
