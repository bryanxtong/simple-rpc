package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.model.fbs.FbsRpcRequest;
import com.bryan.rpc.common.model.fbs.FbsRpcResponse;
import com.bryan.rpc.common.serializer.FbsSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RpcRequest and RpcResponse types supported
 */
public class FbsEncoder extends MessageToByteEncoder<Object> {
    private static final FbsSerializer serializer = new FbsSerializer();

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] bytes = serializer.serialize(msg);
        if (msg.getClass().isAssignableFrom(FbsRpcRequest.class)) {
            out.writeInt(1);
        } else if (msg.getClass().isAssignableFrom(FbsRpcResponse.class)) {
            out.writeInt(2);
        }
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
