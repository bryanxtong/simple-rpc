package com.bryan.rpc.server;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Object Registry to store the serviceName and rpc implementation class instance
 */
public class ObjectServiceRegistry {
    private final Map<String, Object> services = new ConcurrentHashMap<>();

    public void addService(String serviceName, Object serviceImpl) {
        services.put(serviceName, serviceImpl);
    }

    public Object getService(String serviceName) {
        if (serviceName == null) {
            throw new IllegalArgumentException("serviceName cannot be null");
        }
        return services.get(serviceName);
    }

    public Set<String> getAllServices() {
        return services.keySet();
    }
}
