package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.model.fbs.FbsRpcRequest;
import com.bryan.rpc.common.model.fbs.FbsRpcResponse;
import com.bryan.rpc.common.serializer.FbsSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;
/**
 * RpcRequest and RpcResponse types supported
 */
public class FbsDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final FbsSerializer serializer = new FbsSerializer();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 8) {
            return;
        }
        int type = in.readInt();
        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        if (type == 1) {
            out.add(serializer.deserialize(bytes, FbsRpcRequest.class));
        } else if (type == 2) {
            out.add(serializer.deserialize(bytes, FbsRpcResponse.class));
        }
    }
}
