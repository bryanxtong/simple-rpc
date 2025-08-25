package com.bryan.rpc.client.proxy;

import com.bryan.rpc.client.RpcClient;
import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;
import com.bryan.rpc.common.utils.TypeUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class BaseRpcInvocationHandler implements InvocationHandler {
    protected final Class<?> serviceClass;
    protected final RpcClient<RpcRequest, RpcResponse> rpcClient;

    public BaseRpcInvocationHandler(Class<?> serviceClass, RpcClient<RpcRequest, RpcResponse> rpcClient) {
        this.serviceClass = serviceClass;
        this.rpcClient = rpcClient;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = buildRequest(serviceClass, method, args);
        if (CompletableFuture.class.isAssignableFrom(method.getReturnType())) {
            Type wrappedType = TypeUtils.extractWrappedType(method.getGenericReturnType());
            return invokeAsync(request, wrappedType);
        } else {
            return invokeSync(request, method.getGenericReturnType());
        }
    }

    protected abstract Object invokeSync(RpcRequest request, Type genericReturnType) throws Exception;

    protected abstract Object invokeAsync(RpcRequest request, Type genericReturnType) throws Exception;

    public RpcRequest buildRequest(Class<?> serviceClass, Method method, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setMethodName(method.getName());
        request.setClassName(serviceClass.getName());
        request.setParams(args);
        Class<?>[] parameterTypes = method.getParameterTypes();
        request.setParamTypes(parameterTypes);
        request.setServiceName(serviceClass.getName());
        return request;
    }
}
