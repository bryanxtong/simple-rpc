package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.serializer.JdkSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class JdkSerializerDecoder extends ByteToMessageDecoder {
    private final static JdkSerializer jdkSerializer = new JdkSerializer();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object message = jdkSerializer.deserialize(bytes, Object.class);
        out.add(message);
    }
}
