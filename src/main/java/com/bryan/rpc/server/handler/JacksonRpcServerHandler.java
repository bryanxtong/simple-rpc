package com.bryan.rpc.server.handler;

import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;
import com.bryan.rpc.server.JacksonNonIDLRpcMethodInvoker;
import com.bryan.rpc.server.ObjectServiceRegistry;
import com.bryan.rpc.server.RpcMethodInvoker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class JacksonRpcServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(JacksonRpcServerHandler.class);
    private final ObjectServiceRegistry objectServiceRegistry;

    public JacksonRpcServerHandler(ObjectServiceRegistry serviceRegistry) {
        this.objectServiceRegistry = serviceRegistry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("JacksonRpcServerHandler received message：{}", msg);
        RpcRequest request = (RpcRequest) msg;
        try {
            RpcMethodInvoker<RpcRequest,Object> methodInvoker = new JacksonNonIDLRpcMethodInvoker();
            Object resp = methodInvoker.invoke(request, objectServiceRegistry);
            if (RpcResponse.class.isAssignableFrom(resp.getClass())) {
                RpcResponse response = (RpcResponse) resp;
                ctx.writeAndFlush(response);
            } else if (CompletableFuture.class.isAssignableFrom(resp.getClass())) {
                CompletableFuture<RpcResponse> futureResult = (CompletableFuture<RpcResponse>) resp;
                futureResult.whenComplete((result, e) -> {
                    if (e == null) {
                        ctx.writeAndFlush(result);
                    } else {
                        ctx.writeAndFlush(new RpcResponse(null, e.getMessage(), request.getRequestId()));
                    }
                });
            }
        } catch (Exception e) {
            ctx.writeAndFlush(new RpcResponse(null,e.getMessage(),request.getRequestId()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
