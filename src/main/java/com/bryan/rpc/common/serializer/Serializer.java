package com.bryan.rpc.common.serializer;

import com.google.protobuf.ByteString;

public interface Serializer {

    <T> byte[] serialize(T obj) throws Exception;

    <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception;
}
