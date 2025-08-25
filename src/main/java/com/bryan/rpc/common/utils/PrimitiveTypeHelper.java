package com.bryan.rpc.common.utils;

import java.util.HashMap;
import java.util.Map;

public class PrimitiveTypeHelper {
    private static final Map<String, Class<?>> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put("int", int.class);
        TYPE_MAP.put("long", long.class);
        TYPE_MAP.put("double", double.class);
        TYPE_MAP.put("float", float.class);
        TYPE_MAP.put("boolean", boolean.class);
        TYPE_MAP.put("char", char.class);
        TYPE_MAP.put("byte", byte.class);
        TYPE_MAP.put("short", short.class);
        TYPE_MAP.put("void", void.class);
    }

    public static Class<?> getClassForPrimitiveTypeName(String typeName) {
        Class<?> clazz = TYPE_MAP.get(typeName);
        return clazz;
    }
}
