package com.bryan.rpc.server;

import com.bryan.rpc.common.model.thrift.ThriftDynamicParam;
import com.bryan.rpc.common.model.thrift.ThriftRpcRequest;
import com.bryan.rpc.common.model.thrift.ThriftRpcResponse;
import com.bryan.rpc.common.serializer.MessagePackSerializer;
import com.bryan.rpc.common.utils.PrimitiveTypeHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * used for thrift serialized data model and Thrift is using message pack for Its dynamic field serialization
 */
public class ThriftRpcMethodInvoker implements RpcMethodInvoker<ThriftRpcRequest, Object> {
    //messagepack only for ThriftDynamicParam "bytes value" field serialization
    private final MessagePackSerializer serializer = MessagePackSerializer.getInstance();

    @Override
    public Object invoke(ThriftRpcRequest request, ObjectServiceRegistry serviceRegistry) throws Exception {
        Object service = serviceRegistry.getService(request.getServiceName());
        if (service == null) {
            throw new IllegalArgumentException("service not found" + request.getServiceName());
        }

        Class<?> serviceClass = service.getClass();
        String methodName = request.getMethodName();
        List<ThriftDynamicParam> paramsList = request.getParams();
        Class<?>[] paramClassTypes = getParamClassTypes(paramsList);
        Method method = serviceClass.getMethod(methodName, paramClassTypes);
        Class<?> returnType = method.getReturnType();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (CompletableFuture.class.isAssignableFrom(returnType)) {
            try {
                CompletableFuture<?> futureResult = (CompletableFuture<?>) method.invoke(service, getParamValues(paramsList, genericParameterTypes));
                return futureResult.handle((result, ex) -> {
                    //Void.class
                    if (result == null && ex == null) {
                        return new ThriftRpcResponse(null, null, request.getRequestId());
                    }
                    if (ex != null) {
                        return new ThriftRpcResponse(null, ex.getMessage(), request.getRequestId());
                    } else {
                        ByteBuffer byteBuffer = ByteBuffer.wrap(serializer.serialize(result));
                        return new ThriftRpcResponse(byteBuffer, null, request.getRequestId());
                    }
                });
            } catch (Exception e) {
                ThriftRpcResponse errorResponse = new ThriftRpcResponse(null, e.getMessage(), request.getRequestId());
                return CompletableFuture.completedFuture(errorResponse);
            }
        } else {
            try {
                Object result = method.invoke(service, getParamValues(paramsList, genericParameterTypes));
                if (returnType.isPrimitive()) {
                    return processPrimitiveTypesResult(returnType, result, request);
                }
                ThriftRpcResponse thriftRpcResponse = new ThriftRpcResponse(ByteBuffer.wrap(serializer.serialize(result)), null, request.getRequestId());
                return thriftRpcResponse;
            } catch (Exception e) {
                ThriftRpcResponse errorResponse = new ThriftRpcResponse(null, e.getMessage(), request.getRequestId());
                return errorResponse;
            }
        }
    }

    private ThriftRpcResponse processPrimitiveTypesResult(Class<?> returnType, Object result, ThriftRpcRequest request) {
        ByteBuffer byteBuffer = null;
        if (returnType.equals(void.class)) {
            //null
        } else if (returnType.equals(int.class)) {
            int r = (int) result;
            byteBuffer = ByteBuffer.wrap(serializer.serialize(r));
        } else if (returnType.equals(long.class)) {
            long r = (long) result;
            byteBuffer = ByteBuffer.wrap(serializer.serialize(r));
        } else if (returnType.equals(double.class)) {
            double r = (Double) result;
            byteBuffer = ByteBuffer.wrap(serializer.serialize(r));
        } else if (returnType.equals(float.class)) {
            float r = (float) result;
            byteBuffer = ByteBuffer.wrap(serializer.serialize(r));
        } else if (returnType.equals(short.class)) {
            short r = (short) result;
            byteBuffer = ByteBuffer.wrap(serializer.serialize(r));
        } else if (returnType.equals(byte.class)) {
            byte r = (byte) result;
            byteBuffer = ByteBuffer.wrap(serializer.serialize(r));
        } else if (returnType.equals(char.class)) {
            char r = (char) result;
            byteBuffer = ByteBuffer.wrap(serializer.serialize(r));
        } else if (returnType.equals(boolean.class)) {
            boolean r = (boolean) result;
            byteBuffer = ByteBuffer.wrap(serializer.serialize(r));
        }
        ThriftRpcResponse thriftRpcResponse = new ThriftRpcResponse(byteBuffer, null, request.getRequestId());
        return thriftRpcResponse;
    }

    private Object[] getParamValues(List<ThriftDynamicParam> params, Type[] parameterTypes) throws Exception {
        Object[] actualParams = new Object[params.size()];
        for (int i = 0; i < params.size(); i++) {
            ThriftDynamicParam dynamicParam = params.get(i);
            byte[] value = dynamicParam.getValue();
            actualParams[i] = serializer.deserialize(value, parameterTypes[i]);
        }
        return actualParams;
    }

    private Class<?>[] getParamClassTypes(List<ThriftDynamicParam> paramTypes) throws ClassNotFoundException {
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
}
