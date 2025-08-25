package com.bryan.rpc.client.loadbalance;

public class LoadBanlancerFactory {

    public static LoadBalancer createLoadBalancer(String type){
        switch (type){
            case "random": return new RandomLoadBalancer();
            default:throw new IllegalArgumentException("unknown load balancer type" + type);
        }
    }
}
