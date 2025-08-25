package com.bryan.rpc.client.loadbalance;

import com.bryan.rpc.registry.ServiceInstance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer {
    private Random random = new Random();
    @Override
    public ServiceInstance choose(List<ServiceInstance> serviceName) {
        return serviceName.get(random.nextInt(serviceName.size()));
    }
}
