package com.bryan.rpc.server.handler;

import com.bryan.rpc.common.model.protobuf.RpcMessageProto;
import com.bryan.rpc.server.ObjectServiceRegistry;
import com.bryan.rpc.server.ProtobufRpcMethodInvoker;
import com.bryan.rpc.server.RpcMethodInvoker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class ProtobufRpcServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ProtobufRpcServerHandler.class);
    private final ObjectServiceRegistry serviceRegistry;

    public ProtobufRpcServerHandler(ObjectServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcMessageProto.RpcMessageWrapper messageWrapper = (RpcMessageProto.RpcMessageWrapper) msg;
        if (messageWrapper.getType() == RpcMessageProto.ProtobufMessageType.REQUEST) {
            logger.debug("ProtobufRpcServerHandler received message：{}", msg);
            RpcMessageProto.ProtobufRpcRequest request = ((RpcMessageProto.RpcMessageWrapper) msg).getRequest();
            RpcMethodInvoker<RpcMessageProto.ProtobufRpcRequest, Object> methodInvoker = new ProtobufRpcMethodInvoker();
            Object resp = methodInvoker.invoke(request, serviceRegistry);
            if (RpcMessageProto.ProtobufRpcResponse.class.isAssignableFrom(resp.getClass())) {
                try {
                    RpcMessageProto.ProtobufRpcResponse response = (RpcMessageProto.ProtobufRpcResponse) resp;
                    RpcMessageProto.RpcMessageWrapper resultWrapper = RpcMessageProto.RpcMessageWrapper.newBuilder()
                            .setResponse(response)
                            .build();
                    ctx.writeAndFlush(resultWrapper);
                } catch (Exception e) {
                    RpcMessageProto.ProtobufRpcResponse response = RpcMessageProto.ProtobufRpcResponse.newBuilder()
                            .setRequestId(request.getRequestId())
                            .setError("Rpc Called error: " + e.getMessage()).build();
                    RpcMessageProto.RpcMessageWrapper resultWrapper = RpcMessageProto.RpcMessageWrapper.newBuilder()
                            .setResponse(response)
                            .build();
                    ctx.writeAndFlush(resultWrapper);
                }
            } else if (CompletableFuture.class.isAssignableFrom(resp.getClass())) {
                try {
                    CompletableFuture<RpcMessageProto.ProtobufRpcResponse> futureResult = (CompletableFuture<RpcMessageProto.ProtobufRpcResponse>) resp;
                    futureResult.whenComplete((result, throwable) -> {
                        if (throwable == null) {
                            RpcMessageProto.RpcMessageWrapper resultWrapper = RpcMessageProto.RpcMessageWrapper.newBuilder()
                                    .setResponse(result)
                                    .build();
                            ctx.writeAndFlush(resultWrapper);
                        } else {
                            RpcMessageProto.ProtobufRpcResponse response = RpcMessageProto.ProtobufRpcResponse.newBuilder()
                                    .setRequestId(request.getRequestId())
                                    .setError("Rpc Called error: " + throwable.getMessage()).build();
                            RpcMessageProto.RpcMessageWrapper resultWrapper = RpcMessageProto.RpcMessageWrapper.newBuilder()
                                    .setResponse(result)
                                    .build();
                            ctx.writeAndFlush(resultWrapper);
                        }
                    });

                } catch (Exception e) {
                    RpcMessageProto.ProtobufRpcResponse response = RpcMessageProto.ProtobufRpcResponse.newBuilder()
                            .setRequestId(request.getRequestId())
                            .setError("Rpc Called error: " + e.getMessage()).build();
                    RpcMessageProto.RpcMessageWrapper resultWrapper = RpcMessageProto.RpcMessageWrapper.newBuilder()
                            .setResponse(response)
                            .build();
                    ctx.writeAndFlush(resultWrapper);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
