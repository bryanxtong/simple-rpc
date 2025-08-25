package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.serializer.JdkSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class JdkSerializerEncoder extends MessageToByteEncoder<Object> {
    private final static JdkSerializer serializer = new JdkSerializer();

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
