package com.bryan.rpc.common.serializer;

import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.transport.TTransportException;

public class ThriftSerializer implements Serializer {
    private static TSerializer serializer;
    private static TDeserializer deserializer;

    public ThriftSerializer() {
        try {
            serializer = new TSerializer();
            deserializer = new TDeserializer();
        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> byte[] serialize(T obj) throws Exception {
        TBase<?, ?> base = (TBase<?, ?>) obj;
        return serializer.serialize(base);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        T instance = null;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
            deserializer.deserialize((TBase<?, ?>) instance, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }
}
