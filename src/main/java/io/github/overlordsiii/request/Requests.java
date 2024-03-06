package io.github.overlordsiii.request;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.overlordsiii.util.JsonUtils;
import io.github.overlordsiii.util.RequestUtil;

import static io.github.overlordsiii.stockblogger.StockBlogger.API_KEY;

import java.io.IOException;
import java.time.LocalDateTime;

public class Requests {

    private static final String QUERY_RIVAL_PROMPT = "Give me the stock symbols of PUBLIC companies that rival [STOCK_NAME]. Please limit this list to the top 5 rivals and output the list as a numbered list. If the rival is not a public company (one that is not on the NYSE), then omit it. Please ensure this is the rivals STOCK SYMBOL, not their name. Put the name of the company in parenthesis following, like this: \" [STOCK_SYMBOL] ([STOCK_NAME])\"";

    public static Request makeStockNameRequest(String symbol) {
        return new Request("https://api.twelvedata.com/stocks?symbol=" + symbol + "&apikey=" + RequestUtil.getStockAPIKey(), RequestType.GET, null);
    }

    public static Request makeStockPriceRequest(String stockSymbol) {
        return new Request("https://api.twelvedata.com/price?symbol=" + stockSymbol + "&apikey=" + RequestUtil.getStockAPIKey(), RequestType.GET, null);
    }

    public static Request makeSymbolSearchRequest(String stockName) {
        return new Request("https://api.twelvedata.com/symbol_search?symbol=" + stockName, RequestType.GET, null);
    }

    public static Request requestChatGPTRivals(String selectedStock) {
        JsonObject object = new JsonObject();

        object.addProperty("model", "gpt-3.5-turbo");
        JsonArray messages = new JsonArray();
        JsonObject promptObj = new JsonObject();
        promptObj.addProperty("role", "user");
        promptObj.addProperty("content", QUERY_RIVAL_PROMPT.replace("[STOCK_NAME]", selectedStock));
        messages.add(promptObj);
        object.add("messages", messages);

        Request request = new Request("https://api.openai.com/v1/chat/completions", RequestType.POST, object);

        request.addHeader("Authorization", "Bearer " + API_KEY.getConfigOption("chatGptApiKey"));

        return request;
    }

    public static Request makeHistoricalPriceRequest(String symbol) {
        return new Request("https://api.twelvedata.com/time_series?symbol=" + symbol + "&interval=1week&start_date=" + LocalDateTime.now().minusYears(5) + "&apikey=" + RequestUtil.getStockAPIKey(), RequestType.GET, null);
    }

    public static Request makeNewsRequest(String symbol) {
        return new Request("https://api.marketaux.com/v1/news/all?symbols=" + symbol + "&language=en&api_token=" + API_KEY.getConfigOption("marketauxApiKey"), RequestType.GET, null);
    }

    public static Request makeNewsRequestEODHD(String symbol) {
        return new Request("https://eodhd.com/api/news?s=" + symbol + "&limit=10&api_token=" + API_KEY.getConfigOption("eodhdApiKey") + "&fmt=json", RequestType.GET, null);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(JsonUtils.objToString(makeNewsRequestEODHD("AAPL").makeRequestToArray()));
    }


}
