package com.bryan.rpc.common.utils;

import com.bryan.rpc.common.model.fbs.FbsDynamicParam;
import com.bryan.rpc.common.model.fbs.FbsRpcRequest;
import com.bryan.rpc.common.model.fbs.FbsRpcResponse;
import com.bryan.rpc.common.serializer.MessagePackSerializer;
import com.google.flatbuffers.FlatBufferBuilder;
import java.nio.ByteBuffer;
import java.util.List;

public class FbsUtils {

    public static FbsRpcRequest createRpcRequest(String className, String methodName, List<byte[]> paramBytesList, List<String> paramTypes, String requestId, String serviceName) {
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


    public static FbsRpcResponse createRpcResponse(byte[] resultBytes, String error, String requestId) {
        FlatBufferBuilder fbb = new FlatBufferBuilder();
        int valueOffset = fbb.createByteVector(resultBytes);
        int errorOffset = fbb.createString(error);
        int requestIdOffset = fbb.createString(requestId);

        FbsRpcResponse.startFbsRpcResponse(fbb);
        FbsRpcResponse.addResult(fbb, valueOffset);
        FbsRpcResponse.addError(fbb, errorOffset);
        FbsRpcResponse.addRequestId(fbb, requestIdOffset);

        int rootOffset = FbsRpcResponse.endFbsRpcResponse(fbb);
        fbb.finish(rootOffset);
        return FbsRpcResponse.getRootAsFbsRpcResponse(fbb.dataBuffer());
    }

    public static Object[] getParamValues(FbsDynamicParam.Vector vector) throws Exception {
        Object[] actualParams = new Object[vector.length()];
        MessagePackSerializer serializer = new MessagePackSerializer();
        for (int i = 0; i < actualParams.length; i++) {
            FbsDynamicParam dynamicParam = vector.get(i);
            ByteBuffer bb = dynamicParam.valueAsByteBuffer();
            byte[] value = new byte[bb.remaining()];
            bb.get(value);
            String type = dynamicParam.type();
            actualParams[i] = serializer.deserialize(value, Class.forName(type));
        }
        return actualParams;
    }

    public static Class<?>[] getParamClassTypes(FbsDynamicParam.Vector vector) throws ClassNotFoundException {
        Class<?>[] paramClasses = new Class[vector.length()];
        for (int i = 0; i < vector.length(); i++) {
            paramClasses[i] = Class.forName(vector.get(i).type());
        }
        return paramClasses;
    }
}
