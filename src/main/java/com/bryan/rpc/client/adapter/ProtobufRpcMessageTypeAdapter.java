package com.bryan.rpc.client.adapter;

import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;
import com.bryan.rpc.common.model.protobuf.RpcMessageProto;
import com.bryan.rpc.common.serializer.JacksonSerializer;
import com.google.protobuf.ByteString;

public class ProtobufRpcMessageTypeAdapter implements MessageTypeAdapter<RpcMessageProto.RpcMessageWrapper, RpcMessageProto.RpcMessageWrapper> {
    @Override
    public RpcMessageProto.RpcMessageWrapper adapterRequest(RpcRequest request) {
        RpcMessageProto.ProtobufRpcRequest.Builder requestBuilder = RpcMessageProto.ProtobufRpcRequest.newBuilder();
        requestBuilder.setRequestId(request.getRequestId());
        requestBuilder.setClassName(request.getClassName());
        requestBuilder.setMethodName(request.getMethodName());
        requestBuilder.setServiceName(request.getServiceName());
        Object[] params = request.getParams();
        Class<?>[] paramTypes = request.getParamTypes();
        for (int i = 0; i < params.length; i++) {
            requestBuilder.addParamsBuilder()
                    .setValue(ByteString.copyFrom(JacksonSerializer.getInstance().serialize((params[i]))))
                    .setType(paramTypes[i].getName()).build();
        }
        RpcMessageProto.RpcMessageWrapper rpcMessageWrapper = RpcMessageProto.RpcMessageWrapper
                .newBuilder()
                .setRequest(requestBuilder.build())
                .setType(RpcMessageProto.ProtobufMessageType.REQUEST).build();
        return rpcMessageWrapper;
    }

    @Override
    public RpcResponse adapterResponse(RpcMessageProto.RpcMessageWrapper rpcMessageWrapper) {
        RpcMessageProto.ProtobufRpcResponse protobufResponse = rpcMessageWrapper.getResponse();
        String error = protobufResponse.getError();
        ByteString result = protobufResponse.getResult();
        RpcResponse response = new RpcResponse();
        response.setError(error);
        try {
            //Protobuf dynamic field is using jackson, or probuf jsonformat if protobuf message subclasses, so Its string
            if (!result.isEmpty()) {
                String data = result.toStringUtf8();
                response.setResult(data);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
