package io.github.overlordsiii.request;

import com.google.gson.JsonObject;
import io.github.overlordsiii.stockblogger.StockBlogger;
import io.github.overlordsiii.util.JsonUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private final static HttpClient CLIENT = HttpClient.newHttpClient();

    private final String path;

    private final RequestType type;

    private final JsonObject body;

    private final Map<String, String> headers = new HashMap<>();

    public Request(String path, RequestType type, JsonObject body) {
        this.path = path;
        this.type = type;
        this.body = body;
        headers.put("Content-Type", "application/json");
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public JsonObject makeRequest() throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest
                .newBuilder()
                .method(type.name(), JsonUtils.toBody(body));

        headers.forEach(builder::header);

        HttpRequest request = builder
                .uri(URI.create(this.path))
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Made " + this.type.name() + " request to " + path + this.path + " with body: " + JsonUtils.objToString(this.body));

        JsonObject responseObj = JsonUtils.toJsonObj(response.body());
        // uncomment when u need to debug
        //  Main.LOGGER.info("Response: " + JsonUtils.objToString(responseObj));

        return responseObj;
    }

    public JsonObject getBody() {
        return body;
    }

    public String getPath() {
        return path;
    }

    public RequestType getType() {
        return type;
    }
}