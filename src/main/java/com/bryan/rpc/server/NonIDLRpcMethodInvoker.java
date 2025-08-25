package com.bryan.rpc.server;


import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

/**
 * Common method invoker for local method for non-idl serialization format
 */
public class NonIDLRpcMethodInvoker implements RpcMethodInvoker<RpcRequest, Object> {
    @Override
    public Object invoke(RpcRequest request, ObjectServiceRegistry serviceRegistry) throws Exception {
        Object service = serviceRegistry.getService(request.getServiceName());
        if (service == null) {
            throw new IllegalArgumentException("service not found" + request.getServiceName());
        }
        Class<?> serviceClass = service.getClass();
        String methodName = request.getMethodName();
        Object[] params = request.getParams();
        Class<?>[] paramTypes = request.getParamTypes();
        Method method = serviceClass.getMethod(methodName, paramTypes);

        Class<?> returnType = method.getReturnType();
        if (CompletableFuture.class.isAssignableFrom(returnType)) {
            try {
                CompletableFuture<?> futureResult = (CompletableFuture<?>) method.invoke(service, params);
                return futureResult.handle((result, ex) -> {
                    if (ex != null) {
                        return new RpcResponse(null, ex.getMessage(), request.getRequestId());
                    } else {
                        return new RpcResponse(result, null, request.getRequestId());
                    }
                });
            } catch (Exception e) {
                return CompletableFuture.completedFuture(new RpcResponse(null, e.getMessage(), request.getRequestId()));
            }
        } else {
            try {
                Object result = method.invoke(service, params);
                if (returnType.isPrimitive()) {
                    return processPrimitiveTypesResult(returnType, result, request);
                }
                return new RpcResponse(result, null, request.getRequestId());
            } catch (Exception e) {
                return new RpcResponse(null, e.getMessage(), request.getRequestId());
            }
        }
    }

    private RpcResponse processPrimitiveTypesResult(Class<?> returnType, Object result, RpcRequest request) {
        RpcResponse response = null;
        if (returnType.equals(void.class)) {
            response = new RpcResponse(null, null, request.getRequestId());
        } else if (returnType.equals(int.class)) {
            int r = (int) result;
            response = new RpcResponse(r, null, request.getRequestId());
        } else if (returnType.equals(long.class)) {
            long r = (long) result;
            response = new RpcResponse(r, null, request.getRequestId());
        } else if (returnType.equals(double.class)) {
            double r = (double) result;
            response = new RpcResponse(r, null, request.getRequestId());
        } else if (returnType.equals(float.class)) {
            float r = (float) result;
            response = new RpcResponse(r, null, request.getRequestId());
        } else if (returnType.equals(short.class)) {
            short r = (short) result;
            response = new RpcResponse(r, null, request.getRequestId());
        } else if (returnType.equals(byte.class)) {
            byte r = (byte) result;
            response = new RpcResponse(r, null, request.getRequestId());
        } else if (returnType.equals(char.class)) {
            char r = (char) result;
            response = new RpcResponse(r, null, request.getRequestId());
        } else if (returnType.equals(boolean.class)) {
            boolean r = (Boolean) result;
            response = new RpcResponse(r, null, request.getRequestId());
        }
        return response;
    }
}
