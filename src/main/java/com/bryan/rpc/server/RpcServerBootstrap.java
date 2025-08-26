package com.bryan.rpc.server;

import com.bryan.rpc.common.config.ApplicationConfig;
import com.bryan.rpc.registry.*;
import io.netty.channel.Channel;
import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class RpcServerBootstrap {

    private final String host;
    private final int port;
    private final ServiceRegistry serviceRegistry;
    private final ObjectServiceRegistry objectServiceRegistry;
    private NettyRpcServer rpcServer;
    private final CopyOnWriteArrayList<ServiceInstance> activeServiceInstances = new CopyOnWriteArrayList<>();
    //the same server, the same UUID, even different services
    private static final String uuid = UUID.randomUUID().toString();
    private final AtomicBoolean started = new AtomicBoolean(false);

    public RpcServerBootstrap(Class<?> mainClass) throws Exception {
        this.host = ApplicationConfig.getStringProperty("netty.server.host");
        this.port = ApplicationConfig.getIntProperty("netty.server.port");
        String registryType = ApplicationConfig.getStringProperty("netty.registry.type");
        String registryAddr = ApplicationConfig.getStringProperty("netty.registry.address");
        this.serviceRegistry = ServiceRegistryFactory.createServiceRegistry(registryType, registryAddr);
        this.objectServiceRegistry = new ObjectServiceRegistry();
        RpcServer rpcServerAnnotation = mainClass.getAnnotation(RpcServer.class);
        if (rpcServerAnnotation != null) {
            String[] basePackagesToScan = rpcServerAnnotation.scanBasePackages();
            for (String basePackage : basePackagesToScan) {
                if (basePackage == null || basePackage.trim().isEmpty()) {
                    throw new IllegalArgumentException("basePackage must not be null or empty and you can configure it via @RpcServer(scanBasePackages = {\"\",\"\"})");
                }
                scanAndRegisterService(basePackage);
            }
        }
    }

    public void start() throws Exception {
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException("Another Rpc Server Instance has already been started!!!");
        }
        try {
            startNettyRpcServer();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                stop();
            }));
            System.out.println("Netty RPC Server started.");
        } catch (Exception e) {
            started.set(false);
            throw e;
        }
    }

    public void scanAndRegisterService(String basePackage) throws Exception {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RpcService.class);
        for (Class<?> c : classes) {
            RpcService annotation = c.getAnnotation(RpcService.class);
            Object instance = c.getDeclaredConstructor().newInstance();
            this.objectServiceRegistry.addService(annotation.serviceName(), instance);
            ServiceInstance serviceInstance = new ServiceInstance(annotation.serviceName(), uuid, this.host, this.port, new HashMap<>());
            this.serviceRegistry.registerService(serviceInstance);
            activeServiceInstances.add(serviceInstance);
        }
    }

    private void startNettyRpcServer() throws Exception {
        rpcServer = new NettyRpcServer(this.objectServiceRegistry);
        rpcServer.start(this.host, this.port);
    }

    private void stop() {
        if (!started.compareAndSet(true, false)) {
            return;
        }
        System.out.println("Stopping Netty server...");
        for (ServiceInstance serviceInstance : activeServiceInstances) {
            try {
                serviceRegistry.deregisterInstance(serviceInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        activeServiceInstances.clear();
        try{
            if (rpcServer != null) {
                rpcServer.stop();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @throws InterruptedException
     */
    public void awaitTermination() throws InterruptedException {
        if (rpcServer != null) {
            Channel serverChannel = rpcServer.getServerChannel();
            if (serverChannel == null) {
                throw new IllegalStateException("server channel is not initialized");
            }
            serverChannel.closeFuture().sync();
        }
    }
}
