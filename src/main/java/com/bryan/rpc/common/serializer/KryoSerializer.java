package com.bryan.rpc.common.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoSerializer implements Serializer {
    private static final ThreadLocal<Kryo> threadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
/*        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.register(Class[].class);
        kryo.register(Class.class);
        kryo.register(Object[].class);
        kryo.register(Instant.class);
        kryo.register(List.class);*/
        return kryo;
    });

    @Override
    public <T> byte[] serialize(T obj) throws Exception {
        Kryo kryo = threadLocal.get();
        try (Output output = new Output(1024, -1)) {
            kryo.writeClassAndObject(output, obj);
            byte[] data = output.toBytes();
            return data;
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        Kryo kryo = threadLocal.get();
        try (Input input = new Input(bytes)) {
            Object message = kryo.readClassAndObject(input);
            return message == null ? null : clazz.cast(message);
        }
    }
}
