package com.bryan.rpc.client.proxy;

import com.bryan.rpc.client.RpcClient;
import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;
import com.bryan.rpc.common.utils.MessagePackTypeUtils;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class ThriftRpcInvocationHandler extends BaseRpcInvocationHandler {
    public ThriftRpcInvocationHandler(Class<?> serviceClass, RpcClient<RpcRequest, RpcResponse> rpcClient) {
        super(serviceClass, rpcClient);
    }

    @Override
    protected Object invokeSync(RpcRequest request, Type genericReturnType) throws Exception {
        RpcResponse response = rpcClient.send(request);
        if (response.getError() != null && !response.getError().equals("")) {
            throw new RuntimeException(response.getError());
        }
        return processThriftResponseResultDeserialization(response, genericReturnType);
    }

    @Override
    protected Object invokeAsync(RpcRequest request, Type genericReturnType) throws Exception {
        CompletableFuture<RpcResponse> response = rpcClient.sendAsync(request);
        return response.thenApply(rpcResponse -> {
            if (rpcResponse.getError() == null || rpcResponse.getError().equals("")) {
                return processThriftResponseResultDeserialization(rpcResponse, genericReturnType);
            } else {
                throw new RuntimeException(rpcResponse.getError());
            }
        });
    }

    private Object processThriftResponseResultDeserialization(RpcResponse response, Type genericReturnType) {
        if (response.getResult() != null) {
            return MessagePackTypeUtils.fromByteArray((byte[]) response.getResult(), genericReturnType);
        } else {
            return response.getResult();
        }
    }

}
