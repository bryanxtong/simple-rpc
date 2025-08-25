package com.bryan.rpc.client;

import com.bryan.rpc.client.handler.RpcClientHandler;
import com.bryan.rpc.client.loadbalance.LoadBalancer;
import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;
import com.bryan.rpc.registry.ServiceRegistry;
import java.util.concurrent.CompletableFuture;

/**
 * Non - idl generated RpcRequest and RpcResponse
 */
public abstract class CommonRpcClient extends LoadBalancerRpcClient<RpcRequest, RpcResponse> {

    public CommonRpcClient(ServiceRegistry registry, LoadBalancer loadBalancer) {
        super(registry, loadBalancer);
    }

    public RpcResponse send(RpcRequest request) throws Exception{

        channel = routeToChannel(request.getServiceName());
        RpcClientHandler handler = (RpcClientHandler) channel.pipeline().get(this.createRpcClientHandler().getClass());
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        handler.getPendingRequests().put(request.getRequestId(), responseFuture);
        channel.writeAndFlush(request);
        RpcResponse rpcResponse = responseFuture.get();
        return rpcResponse;
    }

    @Override
    public CompletableFuture<RpcResponse> sendAsync(RpcRequest request) throws Exception {
        channel = routeToChannel(request.getServiceName());
        RpcClientHandler handler = (RpcClientHandler) channel.pipeline().get(this.createRpcClientHandler().getClass());
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        handler.getPendingRequests().put(request.getRequestId(), responseFuture);
        channel.writeAndFlush(request);
        return responseFuture;
    }
}
