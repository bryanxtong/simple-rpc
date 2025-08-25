package com.bryan.rpc.client.proxy;

import com.bryan.rpc.client.RpcClient;
import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;
import com.bryan.rpc.common.utils.ProtobufBuilderFactory;
import com.bryan.rpc.common.utils.ProtobufJsonUtils;
import com.bryan.rpc.common.utils.JacksonTypeUtils;
import com.bryan.rpc.common.utils.TypeUtils;
import com.google.protobuf.Message;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class ProtobufRpcInvocationHandler extends BaseRpcInvocationHandler {
    public ProtobufRpcInvocationHandler(Class<?> serviceClass, RpcClient<RpcRequest, RpcResponse> rpcClient) {
        super(serviceClass, rpcClient);
    }

    @Override
    protected Object invokeSync(RpcRequest request, Type genericReturnType) throws Exception {
        RpcResponse response = rpcClient.send(request);
        if (response.getError() != null && !response.getError().equals("")) {
            throw new RuntimeException(response.getError());
        }
        return processProtobufResponseResultDeserialization(response, genericReturnType);
    }

    @Override
    protected Object invokeAsync(RpcRequest request, Type genericReturnType) throws Exception {
        CompletableFuture<RpcResponse> response = rpcClient.sendAsync(request);
        return response.thenApply(rpcResponse -> {
            if (rpcResponse.getError() == null || rpcResponse.getError().equals("")) {
                try {
                    return processProtobufResponseResultDeserialization(rpcResponse, genericReturnType);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException(rpcResponse.getError());
            }
        });
    }

    /**
     * Response.result is in string format(Jackson or probuf jsonformat)
     *
     * @param response
     * @param genericReturnType
     * @return
     * @throws Exception
     */
    private Object processProtobufResponseResultDeserialization(RpcResponse response, Type genericReturnType) throws Exception {
        if (response.getResult() != null) {
            if (Message.class.isAssignableFrom(TypeUtils.extractRawClass(genericReturnType))) {
                Object o = ProtobufJsonUtils.jsonToProtoBuf((String) response.getResult(), ProtobufBuilderFactory.createBuilder(genericReturnType));
                return o;
            }
            return JacksonTypeUtils.fromJson((String) response.getResult(), genericReturnType);
        } else {
            return response.getResult();
        }
    }

}
