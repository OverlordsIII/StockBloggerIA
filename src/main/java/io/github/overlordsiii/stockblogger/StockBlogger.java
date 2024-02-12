package io.github.overlordsiii.stockblogger;

import io.github.overlordsiii.stockblogger.config.PropertiesHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class StockBlogger {

    public static final Logger LOGGER = LogManager.getLogger(StockBlogger.class);

    public static final PropertiesHandler API_KEY = PropertiesHandler
            .builder()
            .addConfigOption("polygonIoApiKey", "")
            .setFileName("api_keys.properties")
            .build();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("What stock do you want information on?: ");

        String stockName = scanner.nextLine();
    }
}
