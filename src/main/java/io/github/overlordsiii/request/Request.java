package io.github.overlordsiii.request;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.overlordsiii.util.JsonUtils;
import io.github.overlordsiii.util.RequestUtil;

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

    private String response;

    private boolean log = true;

    private final Map<String, String> headers = new HashMap<>();

    public Request(String path, RequestType type, JsonObject body) {
        this.path = RequestUtil.urlifyString(path);
        this.type = type;
        this.body = body;
        headers.put("Content-Type", "application/json");
    }

    public Request(String path, RequestType type, JsonObject body, boolean log) {
        this.path = RequestUtil.urlifyString(path);
        this.type = type;
        this.body = body;
        headers.put("Content-Type", "application/json");
        this.log = log;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String executeRequest() throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest
                .newBuilder()
                .method(type.name(), JsonUtils.toBody(body));

        headers.forEach(builder::header);

        HttpRequest request = builder
                .uri(URI.create(this.path))
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (log) {
            System.out.println("Made " + this.type.name() + " request to " + this.path + " with body: " + JsonUtils.elementToString(this.body));
        }

        String responseStr = response.body();

        this.response = responseStr;

        return responseStr;
    }

    public JsonObject makeRequest() throws IOException, InterruptedException {
        if (this.response == null) {
            this.response = executeRequest();
        }

        return JsonUtils.toJsonObj(this.response);
    }

    public JsonArray makeRequestToArray() throws IOException, InterruptedException {
        if (this.response == null) {
            this.response = executeRequest();
        }

        return JsonUtils.toJsonArray(this.response);
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
