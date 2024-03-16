package io.github.overlordsiii.stockblogger.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MiscUtil {

    public static <T, K> T getLastKey(Map<T, K> map) {
        if (map.isEmpty()) {
            return null;
        }

        List<T> keys = new ArrayList<>(map.keySet());

        return keys.get(keys.size() - 1);
    }

    public static <K> Map<Integer, K> reverseMap(Map<Integer, K> map) {
        Integer lastKey = MiscUtil.getLastKey(map);

        if (lastKey == null) {
            return null;
        }

        Map<Integer, K> treeMap = new TreeMap<>(Collections.reverseOrder());

        treeMap.putAll(map);

        List<K> reversedValues = new ArrayList<>(treeMap.values());

        // since we store map in inverted order, we got to invert now
        // in newmap, we will put data starting with the oldest data
        Map<Integer, K> newData = new HashMap<>();

        for (int i = 0; i <= lastKey; i++) {
            newData.put(i, reversedValues.get(i));
        }

        return newData;
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
