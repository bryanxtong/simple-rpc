package com.bryan.rpc.client;

import com.bryan.rpc.client.loadbalance.LoadBalancer;
import com.bryan.rpc.registry.ServiceInstance;
import com.bryan.rpc.registry.ServiceRegistry;
import io.netty.channel.Channel;

import java.util.List;
/**
 * Rpc client with load balance together with registry
 * @param <TRequest>
 * @param <TResponse>
 */
public abstract class LoadBalancerRpcClient<TRequest,TResponse> extends AbstractRpcClient<TRequest,TResponse>{
    private final ServiceRegistry registry;
    private final LoadBalancer loadBalancer;

    public LoadBalancerRpcClient(ServiceRegistry registry, LoadBalancer loadBalancer) {
        this.registry = registry;
        this.loadBalancer = loadBalancer;
    }

    public Channel routeToChannel(String serviceName) throws Exception {
        List<ServiceInstance> serviceInstances = registry.getServiceInstances(serviceName);
        if(serviceInstances == null || serviceInstances.isEmpty()){
            throw new IllegalStateException("No available service: " + serviceName);
        }
        ServiceInstance instance = loadBalancer.choose(serviceInstances);
        return createChannel(instance.getHost(), instance.getPort());
    }
}
