package io.github.overlordsiii.util;

import com.google.gson.*;

import java.net.http.HttpRequest;

public class JsonUtils {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public static String elementToString(JsonElement object) {
        return GSON.toJson(object);
    }

    public static HttpRequest.BodyPublisher toBody(JsonObject object) {
        if (object == null) {
            return HttpRequest.BodyPublishers.noBody();
        }

        return HttpRequest.BodyPublishers.ofString(elementToString(object));
    }

    public static JsonObject toJsonObj(String json) {
        return GSON.fromJson(json, JsonObject.class);
    }

    public static JsonArray toJsonArray(String json) {
        return GSON.fromJson(json, JsonArray.class);
    }

    public static boolean validResponse(String member, JsonObject object) {
        return object.has(member);
    }
}
