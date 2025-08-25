package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.model.Message;
import com.bryan.rpc.common.serializer.MessagePackSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class MessagePackJacksonDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final MessagePackSerializer serializer = new MessagePackSerializer();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object o = serializer.deserialize(data, Message.class);
        out.add(o);
    }
}
