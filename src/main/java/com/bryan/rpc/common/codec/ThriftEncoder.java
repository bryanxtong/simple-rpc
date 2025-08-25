package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.serializer.ThriftSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.thrift.TBase;

public class ThriftEncoder extends MessageToByteEncoder<Object> {
    private static final ThriftSerializer serializer = new ThriftSerializer();

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof TBase<?,?>) {
            TBase<?, ?> obj = (TBase) msg;
            byte[] bytes = serializer.serialize(obj);

            byte[] typeBytes = msg.getClass().getName().getBytes();
            out.writeInt(typeBytes.length);
            out.writeBytes(typeBytes);

            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        } else {}
    }
}
