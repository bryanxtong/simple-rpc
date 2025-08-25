package com.bryan.rpc.client.handler;


import com.bryan.rpc.common.model.protobuf.RpcMessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ProtobufRpcClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(ProtobufRpcClientHandler.class);

    private final Map<String, CompletableFuture<RpcMessageProto.RpcMessageWrapper>> pendingRequests = new ConcurrentHashMap<>();

    public Map<String, CompletableFuture<RpcMessageProto.RpcMessageWrapper>> getPendingRequests() {
        return pendingRequests;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOG.debug("Protobuf RpcClientHandler received msg: {} ", msg);
        if (msg instanceof RpcMessageProto.RpcMessageWrapper wrapper) {
            RpcMessageProto.ProtobufRpcResponse response = wrapper.getResponse();
            String requestId = response.getRequestId();
            CompletableFuture<RpcMessageProto.RpcMessageWrapper> futureResp = pendingRequests.remove(requestId);
            if (null != futureResp) {
                if (!response.getError().isEmpty()) {
                    futureResp.completeExceptionally(new Exception("rpc called error: " + response.getError()));
                } else {
                    futureResp.complete(wrapper);
                }
            }
        }
    }
}
