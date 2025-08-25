package com.bryan.rpc.client.adapter;

import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;
import com.bryan.rpc.common.model.fbs.FbsDynamicParam;
import com.bryan.rpc.common.model.fbs.FbsRpcRequest;
import com.bryan.rpc.common.model.fbs.FbsRpcResponse;
import com.bryan.rpc.common.serializer.MessagePackSerializer;
import com.google.flatbuffers.FlatBufferBuilder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FlatBuffersRpcMessageTypeAdapter implements MessageTypeAdapter<FbsRpcRequest, FbsRpcResponse> {

    private static final MessagePackSerializer serializer = MessagePackSerializer.getInstance();

    @Override
    public FbsRpcRequest adapterRequest(RpcRequest request) {
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Object[] params = request.getParams();
        Class<?>[] clazzParamTypes = request.getParamTypes();

        List<byte[]> paramBytesList = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        for (int i = 0; i < params.length; i++) {
            paramBytesList.add(serializer.serialize(params[i]));
            paramTypes.add(clazzParamTypes[i].getName());
        }
        String requestId = request.getRequestId();
        String serviceName = request.getServiceName();

        FlatBufferBuilder fbb = new FlatBufferBuilder();
        int[] paramOffsets = new int[paramBytesList.size()];
        for (int i = 0; i < paramBytesList.size(); i++) {
            byte[] bytes = paramBytesList.get(i);
            int valueOffset = fbb.createByteVector(bytes);
            int typeOffset = fbb.createString(paramTypes.get(i));
            FbsDynamicParam.startFbsDynamicParam(fbb);
            FbsDynamicParam.addType(fbb, typeOffset);
            FbsDynamicParam.addValue(fbb, valueOffset);
            paramOffsets[i] = FbsDynamicParam.endFbsDynamicParam(fbb);
        }

        int paramsOffset = FbsRpcRequest.createParamsVector(fbb, paramOffsets);
        int classNameOffset = fbb.createString(className);
        int serviceNameOffset = fbb.createString(serviceName);
        int methodOffset = fbb.createString(methodName);
        int requestIdOffset = fbb.createString(requestId);

        FbsRpcRequest.startFbsRpcRequest(fbb);
        FbsRpcRequest.addServiceName(fbb, serviceNameOffset);
        FbsRpcRequest.addMethodName(fbb, methodOffset);
        FbsRpcRequest.addParams(fbb, paramsOffset);
        FbsRpcRequest.addRequestId(fbb, requestIdOffset);
        FbsRpcRequest.addClassName(fbb, classNameOffset);

        int rootOffset = FbsRpcRequest.endFbsRpcRequest(fbb);
        fbb.finish(rootOffset);
        return FbsRpcRequest.getRootAsFbsRpcRequest(fbb.dataBuffer());
    }

    @Override
    public RpcResponse adapterResponse(FbsRpcResponse response) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setError(response.error());
        rpcResponse.setRequestId(response.requestId());

        if (response.resultLength() == 0) {
            //void
            rpcResponse.setResult(null);
        } else {
            ByteBuffer resultByteBuffer = response.resultAsByteBuffer();
            byte[] value = new byte[resultByteBuffer.remaining()];
            resultByteBuffer.get(value);
            rpcResponse.setResult(value);
        }
        return rpcResponse;
    }
}




