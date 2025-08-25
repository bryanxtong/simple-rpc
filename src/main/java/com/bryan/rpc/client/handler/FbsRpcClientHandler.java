package com.bryan.rpc.client.handler;
import com.bryan.rpc.common.model.fbs.FbsRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class FbsRpcClientHandler extends SimpleChannelInboundHandler<FbsRpcResponse> {
    private final static Map<String, CompletableFuture<FbsRpcResponse>> pendingRequests = new ConcurrentHashMap<>();

    public Map<String, CompletableFuture<FbsRpcResponse>> getPendingRequests() {
        return pendingRequests;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FbsRpcResponse response) throws Exception {
        CompletableFuture<FbsRpcResponse> respFuture = pendingRequests.remove(response.requestId());
        if (respFuture != null) {
            if (response.error() == null || response.error().isEmpty()) {
                respFuture.complete(response);
            } else {
                respFuture.completeExceptionally(new RuntimeException(response.error()));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }
}