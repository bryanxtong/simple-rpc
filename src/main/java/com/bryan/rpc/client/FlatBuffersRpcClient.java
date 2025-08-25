package com.bryan.rpc.client;

import com.bryan.rpc.client.adapter.FlatBuffersRpcMessageTypeAdapter;
import com.bryan.rpc.client.adapter.MessageTypeAdapter;
import com.bryan.rpc.common.codec.FbsDecoder;
import com.bryan.rpc.common.codec.FbsEncoder;
import com.bryan.rpc.client.handler.FbsRpcClientHandler;
import com.bryan.rpc.client.loadbalance.LoadBalancer;
import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;
import com.bryan.rpc.common.model.fbs.FbsRpcRequest;
import com.bryan.rpc.common.model.fbs.FbsRpcResponse;
import com.bryan.rpc.registry.ServiceRegistry;
import io.netty.channel.ChannelPipeline;

import java.util.concurrent.CompletableFuture;

public class FlatBuffersRpcClient extends LoadBalancerRpcClient<RpcRequest,RpcResponse> {

    private MessageTypeAdapter<FbsRpcRequest, FbsRpcResponse> messageTypeAdapter = new FlatBuffersRpcMessageTypeAdapter();

    public FlatBuffersRpcClient(ServiceRegistry registry, LoadBalancer loadBalancer) {
        super(registry, loadBalancer);
    }
    @Override
    protected FbsRpcClientHandler createRpcClientHandler() {
        return new FbsRpcClientHandler();
    }

    @Override
    protected void addSerializers(ChannelPipeline pipeline) {
        pipeline.addLast(new FbsDecoder())
                .addLast(new FbsEncoder());
    }

    @Override
    public RpcResponse send(RpcRequest req) throws Exception{
        FbsRpcRequest request = messageTypeAdapter.adapterRequest(req);
        channel = routeToChannel(req.getServiceName());
        FbsRpcClientHandler handler = channel.pipeline().get(this.createRpcClientHandler().getClass());
        CompletableFuture<FbsRpcResponse> responseFuture = new CompletableFuture<>();
        handler.getPendingRequests().put(request.requestId(), responseFuture);
        channel.writeAndFlush(request);
        FbsRpcResponse rpcResponse = responseFuture.get();
        return messageTypeAdapter.adapterResponse(rpcResponse);
    }

    @Override
    public CompletableFuture<RpcResponse> sendAsync(RpcRequest req) throws Exception {
        FbsRpcRequest request = messageTypeAdapter.adapterRequest(req);
        channel = routeToChannel(req.getServiceName());
        FbsRpcClientHandler handler = channel.pipeline().get(this.createRpcClientHandler().getClass());
        CompletableFuture<FbsRpcResponse> responseFuture = new CompletableFuture<>();
        handler.getPendingRequests().put(request.requestId(), responseFuture);
        channel.writeAndFlush(request);
        return responseFuture.thenApply(fbsRpcResponse -> messageTypeAdapter.adapterResponse(fbsRpcResponse))
                .exceptionally(e -> {
            throw new RuntimeException(e.getMessage());
        });
    }
}
