package com.bryan.rpc.server;

import com.bryan.rpc.common.config.ApplicationConfig;
import com.bryan.rpc.registry.*;
import org.reflections.Reflections;

import java.util.*;

public class RpcServerBootstrap {

    private final String host;
    private final int port;
    private final ServiceRegistry serviceRegistry;
    private final ObjectServiceRegistry objectServiceRegistry;

    public RpcServerBootstrap(Class<?> mainClass) throws Exception {
        this.host = ApplicationConfig.getStringProperty("netty.server.host");
        this.port = ApplicationConfig.getIntProperty("netty.server.port");
        String registryType = ApplicationConfig.getStringProperty("netty.registry.type");
        String registryAddr = ApplicationConfig.getStringProperty("netty.registry.address");
        this.serviceRegistry = ServiceRegistryFactory.createServiceRegistry(registryType, registryAddr);
        this.objectServiceRegistry = new ObjectServiceRegistry();
        RpcServer rpcServerAnnotation = mainClass.getAnnotation(RpcServer.class);
        if(rpcServerAnnotation != null){
            String[] basePackagesToScan = rpcServerAnnotation.scanBasePackages();
            for(String  basePackage : basePackagesToScan){
                if (basePackage == null || basePackage.trim().isEmpty()) {
                    throw new IllegalArgumentException("basePackage must not be null or empty and you can configure it via @RpcServer(scanBasePackages = {\"\",\"\"})");
                }
                scanAndRegisterService(basePackage);
            }
        }
    }

    public void start() throws Exception {
        startNettyRpcServer();
    }

    public void scanAndRegisterService(String basePackage) throws Exception {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RpcService.class);
        //the same server, the same UUID, even different services
        String uuid = UUID.randomUUID().toString();
        for (Class<?> c : classes) {
            RpcService annotation = c.getAnnotation(RpcService.class);
            Object instance = c.getDeclaredConstructor().newInstance();
            this.objectServiceRegistry.addService(annotation.serviceName(), instance);
            this.serviceRegistry.registerService(new ServiceInstance(annotation.serviceName(), uuid, this.host, this.port, new HashMap<>()));
        }
    }

    private void startNettyRpcServer() throws Exception {
        NettyRpcServer rpcServer = new NettyRpcServer(this.objectServiceRegistry);
        rpcServer.start(this.host, this.port);
    }
}
