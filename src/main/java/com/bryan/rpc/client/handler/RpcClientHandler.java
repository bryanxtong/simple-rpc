package com.bryan.rpc.client.handler;

import com.bryan.rpc.common.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Common rpc client handler for NON-IDL RpcRequest and RpcResponse
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger LOG = LoggerFactory.getLogger(RpcClientHandler.class);

    private final static Map<String, CompletableFuture<RpcResponse>> pendingRequests = new ConcurrentHashMap<>();
    public Map<String, CompletableFuture<RpcResponse>> getPendingRequests() {
        return pendingRequests;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        LOG.debug("RpcClientHandler received response: {} ", response);
        CompletableFuture<RpcResponse> respFuture = pendingRequests.remove(response.getRequestId());
        if(respFuture != null) {
            if (response.getError()== null || response.getError().isEmpty()) {
                respFuture.complete(response);
            } else {
                respFuture.completeExceptionally(new RuntimeException(response.getError()));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }
}