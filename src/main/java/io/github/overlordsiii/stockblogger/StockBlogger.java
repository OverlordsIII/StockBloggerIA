package io.github.overlordsiii.stockblogger;

import io.github.overlordsiii.stockblogger.config.PropertiesHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

public class StockBlogger {

    public static final Logger LOGGER = LogManager.getLogger(StockBlogger.class);

    public static final PropertiesHandler API_KEY = PropertiesHandler
            .builder()
            .addConfigOption("stockAPIKey", "")
            .setFileName("api_keys.properties")
            .build();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("What stock do you want information on?: ");

        String stockName = scanner.nextLine();

        try {
            URL url = new URL("https://api.twelvedata.com/price?symbol=" + stockName + "&apikey=" + API_KEY.getConfigOption("stockAPIKey"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
