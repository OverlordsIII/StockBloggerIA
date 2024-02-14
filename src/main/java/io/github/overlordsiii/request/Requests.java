package io.github.overlordsiii.request;

import static io.github.overlordsiii.stockblogger.StockBlogger.API_KEY;

public class Requests {

    public static Request makeStockPriceRequest(String stockSymbol) {
        return new Request("https://api.twelvedata.com/price?symbol=" + stockSymbol + "&apikey=" + API_KEY.getConfigOption("stockAPIKey"), RequestType.GET, null);
    }

    public static Request makeSymbolSearchRequest(String stockName) {
        return new Request("https://api.twelvedata.com/symbol_search?symbol=" + stockName, RequestType.GET, null);
    }
}
