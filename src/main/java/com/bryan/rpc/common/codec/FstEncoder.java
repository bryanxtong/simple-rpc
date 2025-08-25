package com.bryan.rpc.common.codec;
import com.bryan.rpc.common.serializer.FstSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class FstEncoder extends MessageToByteEncoder<Object> {
    private static final FstSerializer serializer = new FstSerializer();
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
