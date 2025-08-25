package com.bryan.rpc.common.serializer;

import com.google.protobuf.*;

import java.lang.reflect.Method;

public class ProtoBufSerializer implements Serializer {
    @Override
    public <T> byte[]  serialize(T obj) throws Exception {
        GeneratedMessage message = (GeneratedMessage)obj;
        return message.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        Method method = clazz.getMethod("parseFrom", byte[].class);
        @SuppressWarnings("unchecked")
        T result = (T) method.invoke(null, bytes);
        return result;
    }
}
