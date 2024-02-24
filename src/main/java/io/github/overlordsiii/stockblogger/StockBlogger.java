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

        System.out.println("Price: " + price);

        System.out.println("Executing Chat-GPT Request");

        Request chatgpt = Requests.requestChatGPTRivals(stock);

        JsonObject response = chatgpt.makeRequest();

        JsonObject error = RequestUtil.chatGPTHasError(response);

        if (error != null) {
            if (error.get("message").getAsString().contains("exceeded your current quota")) {
                System.out.println("Chat GPT API Key has exceeded it's current quota! Please update the Chat GPT API Key in api_keys.properties!");
            }  else {
                System.out.println("Error Occurred!");
                System.out.println("Error: \n" + JsonUtils.objToString(error));
            }

            return;
        }


        String[] rivals = RequestUtil.getRivals(response);

        System.out.println("Rivals: ");

        for (String rival : rivals) {
            System.out.println(rival);
        }

        System.out.println("Please answer the following questions: ");
        Map<String, Double> map = RequestUtil.getRivalPrices(rivals);

        System.out.println("These are the popular rivals to " + stock + " and their respective stock prices: ");
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            String s = entry.getKey();
            Double aDouble = entry.getValue();
            System.out.println(RequestUtil.getName(s) + ": " + aDouble);
        }

        List<Double> doubles = RequestUtil.getAllHistoricalStockData(symbol);

        Map<String, List<Double>> rivalsHistoricalData = new HashMap<>();

        for (Map.Entry<String, Double> entry : map.entrySet()) {
            String s = entry.getKey();
            rivalsHistoricalData.put(s, RequestUtil.getAllHistoricalStockData(s));
        }

    }
}
