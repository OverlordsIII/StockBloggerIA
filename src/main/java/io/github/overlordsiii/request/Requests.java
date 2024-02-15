package io.github.overlordsiii.request;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.overlordsiii.stockblogger.StockBlogger;

import static io.github.overlordsiii.stockblogger.StockBlogger.API_KEY;

public class Requests {

    private static final String QUERY_RIVAL_PROMPT = "Give me a list of companies that rival [STOCK_NAME]. Please limit this list to the top 5 rivals and output the list as a numbered list.";

    public static Request makeStockPriceRequest(String stockSymbol) {
        return new Request("https://api.twelvedata.com/price?symbol=" + stockSymbol + "&apikey=" + API_KEY.getConfigOption("stockAPIKey"), RequestType.GET, null);
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
}
