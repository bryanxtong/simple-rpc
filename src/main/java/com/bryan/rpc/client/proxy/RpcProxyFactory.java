package com.bryan.rpc.client.proxy;

import com.bryan.rpc.client.RpcClient;
import com.bryan.rpc.client.RpcClientFactory;
import com.bryan.rpc.client.RpcServiceReference;
import com.bryan.rpc.common.config.ApplicationConfig;
import com.bryan.rpc.client.loadbalance.LoadBalancer;
import com.bryan.rpc.client.loadbalance.LoadBanlancerFactory;
import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;
import com.bryan.rpc.common.model.SerializerType;
import com.bryan.rpc.common.utils.ApplicationConfigUtil;
import com.bryan.rpc.registry.ServiceInstance;
import com.bryan.rpc.registry.ServiceRegistry;
import com.bryan.rpc.registry.ServiceRegistryFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpcProxyFactory {

    private final Map<Class<?>, String> serviceNamesMap = new HashMap<>();
    private final Map<String, RpcClient<RpcRequest, RpcResponse>> rpcClientMap = new HashMap<>();

    public void registerServiceName(Class<?> clazz, String serviceName) {
        serviceNamesMap.put(clazz, serviceName);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> serviceClass) throws Exception {
        String serviceName = serviceNamesMap.getOrDefault(serviceClass, serviceClass.getName());
        RpcServiceReference annotation = serviceClass.getAnnotation(RpcServiceReference.class);
        String type = ApplicationConfig.getStringProperty("netty.registry.type");
        String addr = ApplicationConfig.getStringProperty("netty.registry.address");
        ServiceRegistry serviceRegistry = ServiceRegistryFactory.createServiceRegistry(type, addr);
        //loadbalancer is in class level, if not configured, set to random
        LoadBalancer loadBalancer = LoadBanlancerFactory.createLoadBalancer(annotation == null ? "random" : annotation.loadbalance());
        List<ServiceInstance> serviceInstances = serviceRegistry.getServiceInstances(serviceName);
        if (serviceInstances == null || serviceInstances.isEmpty()) {
            throw new IllegalStateException("no service instance found for service name " + serviceName);
        }
        ServiceInstance serviceInstance = loadBalancer.choose(serviceInstances);
        RpcClient<RpcRequest, RpcResponse> rpcClient = this.getOrCreateRpcClient(loadBalancer, serviceInstance);
        SerializerType serializerType = ApplicationConfigUtil.getSerializeTypeFromConfigFile();
        System.out.println("Application is now using serializerType: " + serializerType.name());
        InvocationHandler handler = null;
        if (serializerType.equals(SerializerType.JSON)) {
            handler = new JacksonRpcInvocationHandler(serviceClass, rpcClient);
        } else if (serializerType.equals(SerializerType.MESSAGEPACK)) {
            handler = new MessagePackRpcInvocationHandler(serviceClass, rpcClient);
        } else if (serializerType.equals(SerializerType.THRIFT)) {
            handler = new ThriftRpcInvocationHandler(serviceClass, rpcClient);
        } else if (serializerType.equals(SerializerType.PROTOBUF)) {
            handler = new ProtobufRpcInvocationHandler(serviceClass, rpcClient);
        } else if (serializerType.equals(SerializerType.FLATBUFFERS)) {
            handler = new FlatBuffersRpcInvocationHandler(serviceClass, rpcClient);
        } else {
            handler = new CommonResultRpcInvocationHandler(serviceClass, rpcClient);
        }
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[]{serviceClass}, handler);
    }

    public RpcClient<RpcRequest, RpcResponse> getOrCreateRpcClient(LoadBalancer loadBalancer, ServiceInstance instance) {
        return rpcClientMap.computeIfAbsent(instance.getInstanceId(), inst -> {
            RpcClientFactory rpcClientFactory = new RpcClientFactory(loadBalancer);
            return rpcClientFactory.createRpcClient();
        });
    }

    public void close() {
        for (RpcClient<RpcRequest, RpcResponse> client : rpcClientMap.values()) {
            if (client != null) {
                client.shutdown();
            }
        }
        rpcClientMap.clear();
    }
}
