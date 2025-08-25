package com.bryan.rpc.server;
import com.bryan.rpc.common.model.fbs.FbsDynamicParam;
import com.bryan.rpc.common.model.fbs.FbsRpcRequest;
import com.bryan.rpc.common.serializer.MessagePackSerializer;
import com.bryan.rpc.common.utils.PrimitiveTypeHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import static com.bryan.rpc.common.utils.FbsUtils.createRpcResponse;

/**
 * used for Fbs serialized data model and Fbs is using message pack for Its dynamic field serialization
 */
public class FbsRpcMethodInvoker implements RpcMethodInvoker<FbsRpcRequest, Object> {
    //messagepack only for FlatBuffers "bytes value" field serialization
    private static final MessagePackSerializer serializer = MessagePackSerializer.getInstance();

    @Override
    public Object invoke(FbsRpcRequest request, ObjectServiceRegistry serviceRegistry) throws Exception {
        Object service = serviceRegistry.getService(request.serviceName());
        if (service == null) {
            throw new IllegalArgumentException("service not found" + request.serviceName());
        }
        Class<?> serviceClass = service.getClass();
        String methodName = request.methodName();
        FbsDynamicParam.Vector paramsList = request.paramsVector();
        Class<?>[] paramClassTypes = getParamClassTypes(paramsList);
        Method method = serviceClass.getMethod(methodName, paramClassTypes);

        Class<?> returnType = method.getReturnType();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (CompletableFuture.class.isAssignableFrom(returnType)) {
            try {
                CompletableFuture<?> futureResult = (CompletableFuture<?>) method.invoke(service, getParamValues(paramsList,genericParameterTypes));
                return futureResult.handle((result, ex) -> {
                    if (ex != null) {
                        return createRpcResponse("".getBytes() ,ex.getMessage(), request.requestId());
                    } else {
                        return createRpcResponse(serializer.serialize(result), "", request.requestId());
                    }
                });
            } catch (Exception e) {
                return CompletableFuture.completedFuture(createRpcResponse("".getBytes(), e.getMessage(), request.requestId()));
            }
        } else {
            try {
                Object result = method.invoke(service, getParamValues(paramsList,genericParameterTypes));
                if (returnType.isPrimitive()) {
                    return processPrimitiveTypesResult(returnType, result, request);
                }
                return createRpcResponse(serializer.serialize(result), "", request.requestId());
            } catch (Exception e) {
                return createRpcResponse("".getBytes(), e.getMessage(), request.requestId());
            }
        }
    }

    private Object processPrimitiveTypesResult(Class<?> returnType, Object result, FbsRpcRequest request) {
        byte[] byteArray = new byte[0];
        if (returnType.equals(void.class)) {
            //cannot be null for fbs
        }
        else if (returnType.equals(int.class)) {
            int r = (int) result;
            byteArray = serializer.serialize(r);
        } else if (returnType.equals(long.class)) {
            long r = (long) result;
            byteArray = serializer.serialize(r);
        } else if (returnType.equals(double.class)) {
            double r = (double) result;
            byteArray = serializer.serialize(r);
        } else if (returnType.equals(float.class)) {
            float r = (float) result;
            byteArray = serializer.serialize(r);
        } else if (returnType.equals(short.class)) {
            short r = (short) result;
            byteArray = serializer.serialize(r);
        } else if (returnType.equals(byte.class)) {
            byte r = (byte) result;
            byteArray = serializer.serialize(r);
        } else if (returnType.equals(char.class)) {
            char r = (char) result;
            byteArray = serializer.serialize(r);
        } else if (returnType.equals(boolean.class)) {
            boolean r = (boolean) result;
            byteArray = serializer.serialize(r);
        }

        return createRpcResponse(byteArray, "", request.requestId());
    }

    public static Object[] getParamValues(FbsDynamicParam.Vector vector,Type[] types) throws Exception {
        Object[] actualParams = new Object[vector.length()];
        MessagePackSerializer serializer = new MessagePackSerializer();
        for (int i = 0; i < actualParams.length; i++) {
            FbsDynamicParam dynamicParam = vector.get(i);
            ByteBuffer bb = dynamicParam.valueAsByteBuffer();
            byte[] value = new byte[bb.remaining()];
            bb.get(value);
            actualParams[i] = serializer.deserialize(value, types[i]);
        }
        return actualParams;
    }

    public static Class<?>[] getParamClassTypes(FbsDynamicParam.Vector vector) throws ClassNotFoundException {
        Class<?>[] paramClasses = new Class[vector.length()];
        for (int i = 0; i < vector.length(); i++) {
            Class<?> classForPrimitiveTypeName = PrimitiveTypeHelper.getClassForPrimitiveTypeName(vector.get(i).type());
            if (classForPrimitiveTypeName == null) {
                paramClasses[i] = Class.forName(vector.get(i).type());
            } else {
                paramClasses[i] = classForPrimitiveTypeName;
            }
        }
        return paramClasses;
    }
}
