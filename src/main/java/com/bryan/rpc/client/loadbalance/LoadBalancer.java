package com.bryan.rpc.client.loadbalance;

import java.util.List;

import com.bryan.rpc.registry.ServiceInstance;

public interface LoadBalancer {
    ServiceInstance choose(List<ServiceInstance> serviceName);
}
