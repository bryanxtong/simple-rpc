package com.bryan.rpc.client.proxy;

import com.bryan.rpc.client.RpcClient;
import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class CommonResultRpcInvocationHandler extends BaseRpcInvocationHandler {
    public CommonResultRpcInvocationHandler(Class<?> serviceClass, RpcClient<RpcRequest, RpcResponse> rpcClient) {
        super(serviceClass, rpcClient);
    }

    @Override
    protected Object invokeSync(RpcRequest request, Type genericReturnType) throws Exception {
        RpcResponse response = rpcClient.send(request);
        if (response.getError() != null && !response.getError().equals("")) {
            throw new RuntimeException(response.getError());
        }
        return response.getResult();
    }

    @Override
    protected Object invokeAsync(RpcRequest request, Type genericReturnType) throws Exception {
        CompletableFuture<RpcResponse> response = rpcClient.sendAsync(request);
        return response.thenApply(rpcResponse -> {
            if (rpcResponse.getError() == null || rpcResponse.getError().equals("")) {
                return rpcResponse.getResult();
            } else {
                throw new RuntimeException(rpcResponse.getError());
            }
        });
    }
}
