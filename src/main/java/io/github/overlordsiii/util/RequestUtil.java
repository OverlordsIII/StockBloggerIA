package io.github.overlordsiii.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.overlordsiii.request.Requests;
import io.github.overlordsiii.stockblogger.StockBlogger;

import java.io.IOException;
import java.util.Scanner;

public class RequestUtil {
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

            System.out.println(i + " - " + name + " (" + object1.get("country").getAsString() + ")");
            i++;
        }

        System.out.println("Enter the number off the stock you desire");

        int num = StockBlogger.SCANNER.nextInt();

        return array.get(num).getAsJsonObject().get("symbol").getAsString();
    }
}
