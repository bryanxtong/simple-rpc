package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.serializer.FstSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class FstDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final FstSerializer serializer = new FstSerializer();

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
        byte[] data = new byte[length];
        in.readBytes(data);
        Object object = serializer.deserialize(data, Object.class);
        out.add(object);
    }
}
