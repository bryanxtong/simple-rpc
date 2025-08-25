package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.serializer.ThriftSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.apache.thrift.TBase;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ThriftDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final ThriftSerializer serializer = new ThriftSerializer();
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        int typeLength = in.readInt();
        if (in.readableBytes() < typeLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] typeBytes = new byte[typeLength];
        in.readBytes(typeBytes);
        String className = new String(typeBytes, StandardCharsets.UTF_8);

        if (in.readableBytes() < 4) {
            in.resetReaderIndex();
            return;
        }
        int length = in.readInt();

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[length];
        in.readBytes(data);
        Class<?> clazz = Class.forName(className);
        TBase<?,?> object= serializer.deserialize(data,(Class<TBase<?,?>>) clazz);
        out.add(object);
    }
}
