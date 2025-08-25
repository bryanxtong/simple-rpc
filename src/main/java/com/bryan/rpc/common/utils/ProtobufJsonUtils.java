package com.bryan.rpc.common.utils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

public class ProtobufJsonUtils {

    public static String protoBufToJson(Message message) throws InvalidProtocolBufferException {
        String jsonMessage = JsonFormat.printer().print(message);
        return jsonMessage;
    }

    public static <T extends Message> T jsonToProtoBuf(String json, Message.Builder builder) throws InvalidProtocolBufferException {
        JsonFormat.parser().merge(json, builder);
        return (T) builder.build();
    }
}
