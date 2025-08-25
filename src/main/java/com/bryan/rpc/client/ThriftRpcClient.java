package com.bryan.rpc.client;

import com.bryan.rpc.client.adapter.MessageTypeAdapter;
import com.bryan.rpc.client.adapter.ThriftRpcMessageTypeAdapter;
import com.bryan.rpc.client.handler.ThriftRpcClientHandler;
import com.bryan.rpc.common.codec.ThriftDecoder;
import com.bryan.rpc.common.codec.ThriftEncoder;
import com.bryan.rpc.client.loadbalance.LoadBalancer;
import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;
import com.bryan.rpc.common.model.thrift.ThriftRpcRequest;
import com.bryan.rpc.common.model.thrift.ThriftRpcResponse;
import com.bryan.rpc.registry.ServiceRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import java.util.concurrent.CompletableFuture;

public class ThriftRpcClient extends LoadBalancerRpcClient<RpcRequest,RpcResponse> {
    private MessageTypeAdapter<ThriftRpcRequest, ThriftRpcResponse> messageTypeAdapter = new ThriftRpcMessageTypeAdapter();

    public ThriftRpcClient(ServiceRegistry registry, LoadBalancer loadBalancer) {
        super(registry, loadBalancer);
    }

    @Override
    protected ChannelHandler createRpcClientHandler() {
        return new ThriftRpcClientHandler();
    }

    @Override
    protected void addSerializers(ChannelPipeline pipeline) {
        pipeline.addLast(new ThriftDecoder())
                .addLast(new ThriftEncoder());
    }

    @Override
    public RpcResponse send(RpcRequest req) throws Exception{
        channel = routeToChannel(req.getServiceName());
        ThriftRpcRequest request = messageTypeAdapter.adapterRequest(req);
        ThriftRpcClientHandler handler = (ThriftRpcClientHandler) channel.pipeline().get(this.createRpcClientHandler().getClass());
        CompletableFuture<ThriftRpcResponse> responseFuture = new CompletableFuture<>();
        handler.getPendingRequests().put(request.getRequestId(), responseFuture);
        channel.writeAndFlush(request);
        ThriftRpcResponse rpcResponse = responseFuture.get();
        return messageTypeAdapter.adapterResponse(rpcResponse);
    }

    @Override
    public CompletableFuture<RpcResponse> sendAsync(RpcRequest req) throws Exception {
        channel = routeToChannel(req.getServiceName());
        ThriftRpcRequest request = messageTypeAdapter.adapterRequest(req);
        ThriftRpcClientHandler handler = (ThriftRpcClientHandler) channel.pipeline().get(this.createRpcClientHandler().getClass());
        CompletableFuture<ThriftRpcResponse> responseFuture = new CompletableFuture<>();
        handler.getPendingRequests().put(request.getRequestId(), responseFuture);
        channel.writeAndFlush(request);
        return responseFuture.thenApply(thriftRpcResponse -> messageTypeAdapter.adapterResponse(thriftRpcResponse))
                .exceptionally(e -> {
            throw new RuntimeException(e.getMessage());
        });
    }
}
