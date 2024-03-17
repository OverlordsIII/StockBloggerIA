package io.github.overlordsiii.stockblogger.api;

import io.github.overlordsiii.stockblogger.util.MiscUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Stock {
    private String symbol;

    private String name;

    private String logoUrl;

    private double price;
    // Format 0 (now), 407.48
    // Format 1 (week ago), etc
    private Map<LocalDateTime, Double> historicalData = new TreeMap<>();

    public Stock(String symbol, String name, Double price, String logoUrl) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.logoUrl = logoUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void addHistoricalDataPoint(double data) {
        LocalDateTime lastKey = MiscUtil.getFirstKey(this.historicalData);

        if (lastKey == null) {
            lastKey = LocalDateTime.now().plusWeeks(1);
        }

        historicalData.put(lastKey.minusWeeks(1), data);
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Map<LocalDateTime, Double> getHistoricalData() {
        return historicalData;
    }
}
