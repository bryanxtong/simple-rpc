package com.bryan.rpc.server.handler;

import com.bryan.rpc.common.model.thrift.ThriftRpcRequest;
import com.bryan.rpc.common.model.thrift.ThriftRpcResponse;
import com.bryan.rpc.server.ObjectServiceRegistry;
import com.bryan.rpc.common.serializer.MessagePackSerializer;
import com.bryan.rpc.server.RpcMethodInvoker;
import com.bryan.rpc.server.ThriftRpcMethodInvoker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class ThriftRpcServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ThriftRpcServerHandler.class);
    private final ObjectServiceRegistry serviceRegistry;
    public ThriftRpcServerHandler(ObjectServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("ThriftRpcServerHandler received:{} ",msg);
        ThriftRpcRequest request = (ThriftRpcRequest) msg;
        try {
            RpcMethodInvoker<ThriftRpcRequest, Object> methodInvoker = new ThriftRpcMethodInvoker();
            Object resp = methodInvoker.invoke(request, serviceRegistry);
            if (ThriftRpcResponse.class.isAssignableFrom(resp.getClass())) {
                ThriftRpcResponse response = (ThriftRpcResponse) resp;
                ctx.writeAndFlush(response);
            } else if (CompletableFuture.class.isAssignableFrom(resp.getClass())) {
                CompletableFuture<ThriftRpcResponse> futureResult = (CompletableFuture<ThriftRpcResponse>) resp;
                futureResult.whenComplete((result, e) -> {
                    if (e == null) {
                        ctx.writeAndFlush(result);
                    } else {
                        ThriftRpcResponse thriftRpcResponse = new ThriftRpcResponse(null, "Rpc Called error: " + e.getMessage(), request.getRequestId());
                        ctx.writeAndFlush(thriftRpcResponse);
                    }
                });
            }
        } catch (Exception e) {
            ThriftRpcResponse response = new ThriftRpcResponse(null, "Rpc Called error: " + e.getMessage(), request.getRequestId());
            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
