package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.model.protobuf.RpcMessageProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProtobufEncoder extends MessageToByteEncoder<RpcMessageProto.RpcMessageWrapper> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessageProto.RpcMessageWrapper msg, ByteBuf out) throws Exception {
        byte[] data = msg.toByteArray();
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
