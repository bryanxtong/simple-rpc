package com.bryan.rpc.common.utils;

import com.bryan.rpc.common.config.ApplicationConfig;
import com.bryan.rpc.common.model.SerializerType;

public class ApplicationConfigUtil {
    public static SerializerType getSerializeTypeFromConfigFile(){
        String serializerProperty = ApplicationConfig.getStringProperty("netty.serializer.type");
        SerializerType serializerType = SerializerType.valueOf(serializerProperty.toUpperCase());
        return serializerType;
    }
}
