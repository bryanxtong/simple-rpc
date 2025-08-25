package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.serializer.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class KryoDecoder extends MessageToMessageDecoder<ByteBuf> {
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
        Object obj = serializer.deserialize(data, Object.class);
        out.add(obj);
    }
}
