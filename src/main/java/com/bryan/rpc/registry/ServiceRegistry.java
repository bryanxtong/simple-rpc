package com.bryan.rpc.registry;

import java.util.List;

public interface ServiceRegistry {

    void registerInstance(ServiceInstance serviceInstance) throws Exception;

    List<ServiceInstance> getInstances(String serviceName) throws Exception;

    public void unregisterInstance(ServiceInstance serviceInstance) throws Exception;

    void close();
}
