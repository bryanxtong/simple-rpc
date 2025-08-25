package com.bryan.rpc.registry;

public class ServiceRegistryFactory {

    public static ServiceRegistry createServiceRegistry(String type, String address) {
        switch (type) {
            case "zookeeper":
                return new ZkServiceRegistry(address);
            default: throw new IllegalArgumentException("Unknown registry type" + type);
        }
    }
}
