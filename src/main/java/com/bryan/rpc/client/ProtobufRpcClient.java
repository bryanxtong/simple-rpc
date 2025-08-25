package com.bryan.rpc.client;

import com.bryan.rpc.client.adapter.MessageTypeAdapter;
import com.bryan.rpc.client.adapter.ProtobufRpcMessageTypeAdapter;
import com.bryan.rpc.client.handler.ProtobufRpcClientHandler;
import com.bryan.rpc.client.loadbalance.LoadBalancer;
import com.bryan.rpc.common.codec.ProtobufDecoder;
import com.bryan.rpc.common.codec.ProtobufEncoder;
import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;
import com.bryan.rpc.common.model.protobuf.RpcMessageProto;
import com.bryan.rpc.registry.ServiceRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import java.util.concurrent.CompletableFuture;

public class ProtobufRpcClient extends LoadBalancerRpcClient<RpcRequest, RpcResponse>{
    private static final MessageTypeAdapter<RpcMessageProto.RpcMessageWrapper, RpcMessageProto.RpcMessageWrapper> messageTypeAdapter = new ProtobufRpcMessageTypeAdapter();

    public ProtobufRpcClient(ServiceRegistry registry, LoadBalancer loadBalancer) {
        super(registry, loadBalancer);
    }

    @Override
    protected ChannelHandler createRpcClientHandler() {
        return new ProtobufRpcClientHandler();
    }

    @Override
    protected void addSerializers(ChannelPipeline pipeline) {
        pipeline.addLast(new ProtobufDecoder());
        pipeline.addLast(new ProtobufEncoder());
    }

    @Override
    public RpcResponse send(RpcRequest req) throws Exception {
        RpcMessageProto.RpcMessageWrapper rpcMessageWrapper = messageTypeAdapter.adapterRequest(req);
        RpcMessageProto.ProtobufRpcRequest protobufRequest = rpcMessageWrapper.getRequest();
        channel = routeToChannel(req.getServiceName());
        ProtobufRpcClientHandler handler = (ProtobufRpcClientHandler) channel.pipeline().get(this.createRpcClientHandler().getClass());
        CompletableFuture<RpcMessageProto.RpcMessageWrapper> responseFuture = new CompletableFuture<>();
        handler.getPendingRequests().put(protobufRequest.getRequestId(), responseFuture);
        //send message wrapper which includes protobuf rpcrequest
        channel.writeAndFlush(rpcMessageWrapper);
        RpcMessageProto.RpcMessageWrapper rpcResponse = responseFuture.get();
        RpcResponse response = messageTypeAdapter.adapterResponse(rpcResponse);
        return response;
    }

    @Override
    public CompletableFuture<RpcResponse> sendAsync(RpcRequest req) throws Exception {
        RpcMessageProto.RpcMessageWrapper rpcMessageWrapper = messageTypeAdapter.adapterRequest(req);
        RpcMessageProto.ProtobufRpcRequest protobufRequest = rpcMessageWrapper.getRequest();
        channel = routeToChannel(req.getServiceName());
        ProtobufRpcClientHandler handler = (ProtobufRpcClientHandler) channel.pipeline().get(this.createRpcClientHandler().getClass());
        CompletableFuture<RpcMessageProto.RpcMessageWrapper> responseFuture = new CompletableFuture<>();
        handler.getPendingRequests().put(protobufRequest.getRequestId(), responseFuture);
        //send message wrapper which includes protobuf rpcrequest
        channel.writeAndFlush(rpcMessageWrapper);
        return responseFuture.thenApply(wrapper -> messageTypeAdapter.adapterResponse(wrapper)).exceptionally(e -> {
            throw new RuntimeException(e.getMessage());
        });
    }
}
