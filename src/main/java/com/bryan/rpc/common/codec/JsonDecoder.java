package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.model.Message;
import com.bryan.rpc.common.serializer.JacksonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;
public class JsonDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final JacksonSerializer serializer = JacksonSerializer.getInstance();
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int dataLength = in.readInt();
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Message message = serializer.deserialize(data, Message.class);
        out.add(message);
    }
}
