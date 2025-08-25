package com.bryan.rpc.registry;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ServiceInstance {
    private final String instanceId;
    private final String serviceName;
    private final String host;
    private final int port;
    private final Map<String, String> metadata;

    public ServiceInstance(String serviceName, String instanceId, String host, int port, Map<String, String> metadata) {
        this.metadata = metadata;
        if(instanceId == null|| instanceId.isEmpty()){
            this.instanceId = UUID.randomUUID().toString();
        }else{
            this.instanceId = instanceId;
        }
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }


    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "ServiceInstance{" +
                "serviceInstanceId='" + instanceId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", metadata=" + metadata +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ServiceInstance that = (ServiceInstance) o;
        return port == that.port && Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
