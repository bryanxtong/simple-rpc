package com.bryan.rpc.client.proxy;

import com.bryan.rpc.client.RpcClient;
import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;
import com.bryan.rpc.common.utils.JacksonTypeUtils;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class JacksonRpcInvocationHandler extends BaseRpcInvocationHandler {
    public JacksonRpcInvocationHandler(Class<?> serviceClass, RpcClient<RpcRequest, RpcResponse> rpcClient) {
        super(serviceClass, rpcClient);
    }

    @Override
    protected Object invokeSync(RpcRequest request, Type genericReturnType) throws Exception {
        RpcResponse response = rpcClient.send(request);
        if (response.getError() != null && !response.getError().equals("")) {
            throw new RuntimeException(response.getError());
        }
        return processJacksonResponse(response, genericReturnType);
    }

    @Override
    protected Object invokeAsync(RpcRequest request, Type genericReturnType) throws Exception {
        CompletableFuture<RpcResponse> response = rpcClient.sendAsync(request);
        return response.thenApply(rpcResponse -> {
            if (rpcResponse.getError() == null || rpcResponse.getError().equals("")) {
                return processJacksonResponse(rpcResponse, genericReturnType);
            } else {
                throw new RuntimeException(rpcResponse.getError());
            }
        });
    }

    /**
     * RpcResponse#result should be deserialized to a specified type
     *
     * @param response
     * @param genericReturnType
     * @return
     */
    private Object processJacksonResponse(RpcResponse response, Type genericReturnType) {
        return JacksonTypeUtils.convertValue(response.getResult(), genericReturnType);
    }
}
