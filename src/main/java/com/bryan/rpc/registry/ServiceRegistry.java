package com.bryan.rpc.registry;

import java.util.List;

public interface ServiceRegistry {

    void registerService(ServiceInstance serviceInstance) throws Exception;

    List<ServiceInstance> getServiceInstances(String serviceName) throws Exception;

    public void deregisterInstance(ServiceInstance serviceInstance) throws Exception;

    void close();
}
