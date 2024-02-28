package io.github.overlordsiii.api;

import io.github.overlordsiii.util.MiscUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stock {
    private String symbol;

    private String name;

    private double price;
    // Format 0 (now), 407.48
    // Format 1 (month ago), etc
    private Map<Integer, Double> historicalData = new HashMap<>();

    public Stock(String symbol, String name, Double price, Map<Integer, Double> historicalData) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.historicalData = historicalData;
    }

    public Stock(String symbol, String name, Double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public Stock() {

    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void addHistoricalDataPoint(double data) {
        Integer lastKey = MiscUtil.getLastKey(this.historicalData);

        if (lastKey == null) {
            lastKey = -1;
        }

        historicalData.put(1 + lastKey, data);
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

    public Map<Integer, Double> getHistoricalData() {
        return historicalData;
    }
}
