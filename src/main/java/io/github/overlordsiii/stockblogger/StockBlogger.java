package io.github.overlordsiii.stockblogger;

import com.google.gson.JsonObject;
import io.github.overlordsiii.request.Request;
import io.github.overlordsiii.request.Requests;
import io.github.overlordsiii.stockblogger.config.PropertiesHandler;
import io.github.overlordsiii.util.JsonUtils;
import io.github.overlordsiii.util.RequestUtil;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

public class StockBlogger {

    public static final Logger LOGGER = Logger.getLogger("StockBlogger");

    public static final PropertiesHandler API_KEY = PropertiesHandler
            .builder()
            .addConfigOption("stockAPIKey", "")
            .setFileName("api_keys.properties")
            .build();

    public static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("What stock do you want to analyze?");

        String stock = SCANNER.nextLine();

        String symbol = RequestUtil.getStockSymbol(stock);

        Request request = Requests.makeStockPriceRequest(symbol);

        JsonObject object = request.makeRequest();

        if (JsonUtils.validResponse("price", object)) {
            double price = Double.parseDouble(request.makeRequest().get("price").getAsString());

            System.out.println("Price: " + price);
        } else {
            System.out.println(JsonUtils.objToString(object));
        }


    }
}
