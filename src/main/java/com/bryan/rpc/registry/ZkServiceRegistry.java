package com.bryan.rpc.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.*;

public class ZkServiceRegistry implements ServiceRegistry {
    private final CuratorFramework curator;
    private final Set<String> registeredPath = new HashSet<>();

    public ZkServiceRegistry(String address) {
        curator = CuratorFrameworkFactory
                .builder()
                .connectString(address)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curator.start();
        curator.getConnectionStateListenable().addListener((curatorFramework, connectionState) -> {
            if (connectionState == ConnectionState.LOST || connectionState == ConnectionState.SUSPENDED) {
                registeredPath.clear();
            }
        });
    }

    public ZkServiceRegistry() {
        this("localhost" + ":" + 2181);
    }

    @Deprecated
    private void cleanupAllRegisteredServices() {
        try {
            for (String path : registeredPath) {
                if (curator.checkExists().forPath(path) != null) {
                    curator.delete().deletingChildrenIfNeeded().forPath(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerInstance(ServiceInstance serviceInstance) throws Exception {
        String servicePath = "/rpc/services/" + serviceInstance.getServiceName();
        String instancePath = servicePath + "/" + serviceInstance.getInstanceId();

        if (curator.checkExists().forPath(servicePath) == null) {
            curator.create().creatingParentsIfNeeded().forPath(servicePath);
        }
        curator.create()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(instancePath, (serviceInstance.getHost() + ":" + serviceInstance.getPort()).getBytes());
        registeredPath.add(instancePath);
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceName) throws Exception {
        List<ServiceInstance> serviceInstances = new ArrayList<>();
        String servicePath = "/rpc/services/" + serviceName;
        List<String> instances = curator.getChildren().forPath(servicePath);
        for (String instanceId : instances) {
            byte[] bytes = curator.getData().forPath(servicePath + "/" + instanceId);
            String data = new String(bytes);
            String[] split = data.split(":");
            ServiceInstance serviceInstance = new ServiceInstance(serviceName, instanceId, split[0], Integer.parseInt(split[1]), new LinkedHashMap<>());
            serviceInstances.add(serviceInstance);
        }
        return serviceInstances;
    }

    @Override
    public void unregisterInstance(ServiceInstance serviceInstance) throws Exception {
        String instancePath = "/rpc/services/" + serviceInstance.getServiceName() + "/" + serviceInstance.getInstanceId();
        if (curator.checkExists().forPath(instancePath) != null) {
            curator.delete().deletingChildrenIfNeeded().forPath(instancePath);
        }
    }

    public String getInstance(String serviceName, String instanceId) throws Exception {
        String servicePath = "/rpc/services/" + serviceName + "/" + instanceId;
        return new String(curator.getData().forPath(servicePath));
    }

    public void close() {
        curator.close();
    }
}
