package io.github.overlordsiii.stockblogger.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MiscUtil {

    public static <T, K> T getFirstKey(Map<T, K> map) {
        if (map.isEmpty()) {
            return null;
        }

        List<T> keys = new ArrayList<>(map.keySet());

        return keys.getFirst();
    }


    public static  <T, K> String getFormattedMap(Map<T, K> map, Function<T, String> keyMapper, Function<K, JsonElement> valueMapper) {
        JsonObject object = new JsonObject();
        map.forEach((t, k) -> {
            object.add(keyMapper.apply(t), valueMapper.apply(k));
        });

        return JsonUtils.elementToString(object);
    }

    public static boolean isNum(String num) {
        try {
            Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }



}
