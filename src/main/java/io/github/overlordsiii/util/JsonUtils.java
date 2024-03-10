package io.github.overlordsiii.util;

import com.google.gson.*;
import io.github.overlordsiii.api.Article;
import io.github.overlordsiii.api.Stock;
import io.github.overlordsiii.stockblogger.config.PropertiesHandler;
import io.github.overlordsiii.util.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
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

    public static <T> JsonElement toJsonElement(T obj) {
        return GSON.toJsonTree(obj);
    }

    public static <T> T fromJsonElement(JsonElement element, Class<T> clazz) {
        return GSON.fromJson(element, clazz);
    }

    public static JsonArray toJsonArray(String json) {
        return GSON.fromJson(json, JsonArray.class);
    }

    public static boolean validResponse(String member, JsonObject object) {
        return object.has(member);
    }

    public static <T> List<T> getObjects(JsonArray rivalsArray, Class<T> clazz) {
        List<T> list = new ArrayList<>();

        for (JsonElement element : rivalsArray) {
            T obj = fromJsonElement(element, clazz);
            list.add(obj);
        }

        return list;
    }

    public static <T> JsonArray toJsonArray(List<T> objects) {
        JsonArray array = new JsonArray();

        for (T object : objects) {
            array.add(toJsonElement(object));
        }

        return array;
    }


    public static void createJsonTestFile(Stock selectedStock, List<Stock> rivals, List<Article> articles) throws IOException {
        JsonElement element = JsonUtils.toJsonElement(selectedStock);

        JsonArray rivalsArray = JsonUtils.toJsonArray(rivals);

        JsonArray articlesArray = JsonUtils.toJsonArray(articles);

        JsonObject object = new JsonObject();
        object.add("selectedStock", element);
        object.add("rivals", rivalsArray);
        object.add("articles", articlesArray);

        Path path = PropertiesHandler.CONFIG_HOME_DIRECTORY.resolve(selectedStock.getSymbol() + ".json");

        String json = elementToString(object);

        Files.writeString(path, json);
    }
}
