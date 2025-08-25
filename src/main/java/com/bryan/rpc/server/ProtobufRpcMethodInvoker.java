package com.bryan.rpc.server;

import com.bryan.rpc.common.model.protobuf.RpcMessageProto;
import com.bryan.rpc.common.serializer.JacksonSerializer;
import com.bryan.rpc.common.utils.ProtobufJsonUtils;
import com.bryan.rpc.common.utils.PrimitiveTypeHelper;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * used for protobuf serialized data model, for dynamic result filed ,It is using jackson serializer
 */
public class ProtobufRpcMethodInvoker implements RpcMethodInvoker<RpcMessageProto.ProtobufRpcRequest, Object> {
    private static final JacksonSerializer jacksonSerializer = JacksonSerializer.getInstance();

    public Object invoke(RpcMessageProto.ProtobufRpcRequest rpcRequest, ObjectServiceRegistry serviceRegistry) throws Exception {
        Object service = serviceRegistry.getService(rpcRequest.getServiceName());
        if (service == null) {
            throw new IllegalArgumentException("service not found" + rpcRequest.getServiceName());
        }

        Class<?> serviceClass = service.getClass();
        String methodName = rpcRequest.getMethodName();
        List<RpcMessageProto.ProtobufDynamicParam> paramsList = rpcRequest.getParamsList();
        Class<?>[] paramClassTypes = getParamClassTypes(paramsList);
        Class<?>[] paramTypes = new Class[paramsList.size()];
        Object[] params = new Object[paramsList.size()];
        Method method = serviceClass.getMethod(methodName, paramClassTypes);
        Class<?> returnType = method.getReturnType();
        Type[] genericParameterTypes = method.getGenericParameterTypes();

        if (CompletableFuture.class.isAssignableFrom(returnType)) {
            CompletableFuture<?> futureResult = (CompletableFuture<?>) method.invoke(service, getParamValues(paramsList, genericParameterTypes));
            return futureResult.handle((result, ex) -> {
                if (ex != null) {
                    try {
                        return buildProtobufErrorResponse(rpcRequest.getRequestId(), ex.getMessage());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        return buildProtobufResponse(rpcRequest.getRequestId(), result);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            try {
                Object result = method.invoke(service, getParamValues(paramsList, genericParameterTypes));
                if (returnType.isPrimitive()) {
                    ByteString bytes = processPrimitiveTypesResult(returnType, result);
                    if(bytes == null){
                        return RpcMessageProto.ProtobufRpcResponse.newBuilder().setRequestId(rpcRequest.getRequestId()).build();
                    }else{
                        return RpcMessageProto.ProtobufRpcResponse.newBuilder().setResult(bytes).setRequestId(rpcRequest.getRequestId()).build();
                    }
                } else {
                    return buildProtobufResponse(rpcRequest.getRequestId(), result);
                }
            } catch (Exception e) {
                return buildProtobufErrorResponse(rpcRequest.getRequestId(), e.getMessage());
            }
        }
    }

    private Object[] getParamValues(List<RpcMessageProto.ProtobufDynamicParam> params, Type[] genericParameterTypes) {
        Object[] actualParams = new Object[params.size()];
        for (int i = 0; i < params.size(); i++) {
            RpcMessageProto.ProtobufDynamicParam dynamicParam = params.get(i);
            byte[] value = dynamicParam.getValue().toByteArray();
            actualParams[i] = jacksonSerializer.deserialize(value, genericParameterTypes[i]);
        }
        return actualParams;
    }

    private Class<?>[] getParamClassTypes(List<RpcMessageProto.ProtobufDynamicParam> paramTypes) throws ClassNotFoundException {
        Class<?>[] paramClasses = new Class[paramTypes.size()];
        for (int i = 0; i < paramTypes.size(); i++) {
            Class<?> classForPrimitiveTypeName = PrimitiveTypeHelper.getClassForPrimitiveTypeName(paramTypes.get(i).getType());
            if (classForPrimitiveTypeName == null) {
                paramClasses[i] = Class.forName(paramTypes.get(i).getType());
            } else {
                paramClasses[i] = classForPrimitiveTypeName;
            }
        }
        return paramClasses;
    }

    private ByteString processPrimitiveTypesResult(Class<?> returnType, Object result) {
        ByteString bytes = null;
        if (returnType.equals(void.class)) {
            //bytes = null;
        }
        else if (returnType.equals(int.class)) {
            int r = (int) result;
            bytes = ByteString.copyFrom(jacksonSerializer.serialize(r));
        } else if (returnType.equals(long.class)) {
            long r = (long) result;
            bytes = ByteString.copyFrom(jacksonSerializer.serialize(r));
        } else if (returnType.equals(double.class)) {
            double r = (double) result;
            bytes = ByteString.copyFrom(jacksonSerializer.serialize(r));
        } else if (returnType.equals(float.class)) {
            float r = (float) result;
            bytes = ByteString.copyFrom(jacksonSerializer.serialize(r));
        } else if (returnType.equals(short.class)) {
            short r = (short) result;
            bytes = ByteString.copyFrom(jacksonSerializer.serialize(r));
        } else if (returnType.equals(byte.class)) {
            byte r = (byte) result;
            bytes = ByteString.copyFrom(jacksonSerializer.serialize(r));
        } else if (returnType.equals(char.class)) {
            char r = (char) result;
            bytes = ByteString.copyFrom(jacksonSerializer.serialize(r));
        } else if (returnType.equals(boolean.class)) {
            boolean r = (boolean) result;
            bytes = ByteString.copyFrom(jacksonSerializer.serialize(r));
        }
        return bytes;
    }

    public static RpcMessageProto.ProtobufRpcResponse buildProtobufResponse(String requestId, Object result) throws Exception {
        RpcMessageProto.ProtobufRpcResponse.Builder responseBuilder = RpcMessageProto.ProtobufRpcResponse.newBuilder();
        if (result != null) {
            if (result instanceof Message) {
                responseBuilder.setRequestId(requestId).setResult(ByteString.copyFrom(ProtobufJsonUtils.protoBufToJson((Message) result), Charset.defaultCharset()));
            } else {
                responseBuilder.setRequestId(requestId).setResult(ByteString.copyFrom(JacksonSerializer.getInstance().serialize(result)));
            }
        }else{
            responseBuilder.setRequestId(requestId);
        }
        return responseBuilder.build();
    }

    public static RpcMessageProto.ProtobufRpcResponse buildProtobufErrorResponse(String requestId, String error) throws Exception {
        RpcMessageProto.ProtobufRpcResponse.Builder responseBuilder = RpcMessageProto.ProtobufRpcResponse.newBuilder();
        if (error != null) {
            responseBuilder.setRequestId(requestId).setError(error.toString());
        }
        return responseBuilder.build();
    }

}
