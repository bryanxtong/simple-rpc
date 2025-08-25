package com.bryan.rpc.client.adapter;

import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;
import com.bryan.rpc.common.model.thrift.ThriftDynamicParam;
import com.bryan.rpc.common.model.thrift.ThriftRpcRequest;
import com.bryan.rpc.common.model.thrift.ThriftRpcResponse;
import com.bryan.rpc.common.serializer.MessagePackSerializer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ThriftRpcMessageTypeAdapter implements MessageTypeAdapter<ThriftRpcRequest, ThriftRpcResponse> {
    @Override
    public ThriftRpcRequest adapterRequest(RpcRequest request) {
        ThriftRpcRequest thiftRequest = new ThriftRpcRequest();
        thiftRequest.setClassName(request.getClassName());
        thiftRequest.setMethodName(request.getMethodName());
        thiftRequest.setRequestId(UUID.randomUUID().toString());
        thiftRequest.setServiceName(request.getServiceName());

        Object[] params = request.getParams();
        Class<?>[] paramTypes = request.getParamTypes();
        List<ThriftDynamicParam> dynamicParams = new ArrayList<>();

        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            Class<?> paramType = paramTypes[i];
            ThriftDynamicParam dynamicParam = new ThriftDynamicParam();
            dynamicParam.setValue(ByteBuffer.wrap(MessagePackSerializer.getInstance().serialize(param)));
            dynamicParam.setType(paramType.getName());
            dynamicParams.add(dynamicParam);
        }
        thiftRequest.setParams(dynamicParams);
        return thiftRequest;
    }

    @Override
    public RpcResponse adapterResponse(ThriftRpcResponse response) {
        String error = response.getError();
        byte[] bytes = response.getValue();
        String requestId = response.getRequestId();
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(requestId);
        try {
            if (bytes != null) {
                rpcResponse.setResult(bytes);
            }
            rpcResponse.setError(error);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rpcResponse;
    }

}
