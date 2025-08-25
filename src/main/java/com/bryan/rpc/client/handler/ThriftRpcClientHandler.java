package com.bryan.rpc.client.handler;

import com.bryan.rpc.common.model.thrift.ThriftRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ThriftRpcClientHandler extends SimpleChannelInboundHandler<ThriftRpcResponse> {
    private static final Logger LOG = LoggerFactory.getLogger(ThriftRpcClientHandler.class);
    private final static Map<String, CompletableFuture<ThriftRpcResponse>> pendingRequests = new ConcurrentHashMap<>();
    public Map<String, CompletableFuture<ThriftRpcResponse>> getPendingRequests() {
        return pendingRequests;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ThriftRpcResponse msg) throws Exception {
        LOG.debug("Thrift RpcClientHandler received response: {} ", msg);
        CompletableFuture<ThriftRpcResponse> respFuture = pendingRequests.remove(msg.getRequestId());
        if(respFuture != null) {
            if(msg.getError()== null || msg.getError().isEmpty()){
                respFuture.complete(msg);
            }else{
                respFuture.completeExceptionally(new RuntimeException(msg.getError()));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }
}