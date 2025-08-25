package com.bryan.rpc.common.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.lang.reflect.Type;

public class MessagePackSerializer implements Serializer {
    private static final MessagePackSerializer instance = new MessagePackSerializer();
    private static final ObjectMapper mapper = new ObjectMapper(new MessagePackFactory())
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); //Instant will have decimal error if it is set in timestamp

    public static MessagePackSerializer getInstance() {
        return instance;
    }

    public String serializeToString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] serialize(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        return mapper.readValue(bytes, clazz);
    }

    public <T> T deserialize(byte[] bytes, Type type) throws Exception {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        return mapper.readValue(bytes, javaType);
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> typeRef) {
        if (fromValue == null) {
            return null;
        }
        return mapper.convertValue(fromValue, typeRef);
    }
}
