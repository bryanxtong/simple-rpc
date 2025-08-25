package com.bryan.rpc.common.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ApplicationConfig {
    private static final String CONFIG_FILE_PATH = "config.yml";
    private static final Map<String, Object> config;

    static {
        Yaml yaml = new Yaml();
        InputStream defaultConfigStream = ApplicationConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE_PATH);
        Map<String, Object> defaultConfigMap = defaultConfigStream != null ? yaml.load(defaultConfigStream) : new HashMap<>();
        InputStream userConfigStream = ApplicationConfig.class.getClassLoader().getResourceAsStream("application.yml");
        Map<String, Object> userConfigMap = userConfigStream != null ? yaml.load(userConfigStream) : new HashMap<>();
        config = mergeConfig(defaultConfigMap, userConfigMap);
    }

    private static Map<String, Object> mergeConfig(Map<String, Object> defaultConfigMap, Map<String, Object> userConfigMap) {

        Map<String, Object> result = new HashMap<>(defaultConfigMap);
        if (userConfigMap == null) {
            return result;
        }
        for (Map.Entry<String, Object> entry : userConfigMap.entrySet()) {
            String key = entry.getKey();
            Object entryValue = entry.getValue();
            Object defaultValue = defaultConfigMap.get(key);
            if (defaultValue instanceof Map && entryValue instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> userSubMap = (Map<String, Object>) entry.getValue();
                @SuppressWarnings("unchecked")
                Map<String, Object> defaultSubMap = (Map<String, Object>) defaultConfigMap.get(key);
                result.put(key, mergeConfig(userSubMap, defaultSubMap));
            } else {
                result.put(key, entryValue);
            }
        }
        return result;
    }

    private static Object getNestedValue(Map<String, Object> map, String[] keys) {
        if (keys.length == 0) {
            return null;
        }
        if (keys.length == 1) {
            return map.get(keys[0]);
        }
        Object nextLevel = map.get(keys[0]);
        if (nextLevel instanceof Map) {
            return getNestedValue((Map<String, Object>) nextLevel, subArray(keys, 1));
        }
        return null;
    }

    private static String[] subArray(String[] arr, int start) {
        int length = arr.length - start;
        String[] result = new String[length];
        System.arraycopy(arr, start, result, 0, length);
        return result;
    }

    private static Object getConfig(String key) {
        String[] keys = key.split("\\.");
        Object nestedValue = getNestedValue(config, keys);
        return nestedValue;
    }

    public static Integer getIntProperty(String key) {
        Object o = getConfig(key);
        if (o instanceof Number) {
            return ((Number) o).intValue();
        } else {
            return Integer.parseInt(o.toString());
        }
    }

    public static String getStringProperty(String key) {
        Object o = getConfig(key);
        return o != null ? o.toString() : null;
    }
}
