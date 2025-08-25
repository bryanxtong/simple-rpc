package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.serializer.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class KryoEncoder extends MessageToByteEncoder<Object> {
    private static final KryoSerializer serializer = new KryoSerializer();
/*    private static final ThreadLocal<Kryo> threadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.register(Class[].class);
        kryo.register(Class.class);
        kryo.register(Object[].class);
        kryo.register(Instant.class);
        return kryo;
    });*/
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] data = serializer.serialize(msg);
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
