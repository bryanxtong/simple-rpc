package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.model.Message;
import com.bryan.rpc.common.serializer.MessagePackSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessagePackJacksonEncoder extends MessageToByteEncoder<Message> {
    private static final MessagePackSerializer serializer = new MessagePackSerializer();

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
