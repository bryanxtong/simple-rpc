package com.bryan.rpc.client;

import com.bryan.rpc.client.loadbalance.LoadBalancer;
import com.bryan.rpc.common.config.ApplicationConfig;
import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;
import com.bryan.rpc.common.model.SerializerType;
import com.bryan.rpc.common.utils.ApplicationConfigUtil;
import com.bryan.rpc.registry.ServiceRegistry;
import com.bryan.rpc.registry.ServiceRegistryFactory;

import java.util.Objects;

public class RpcClientFactory {

    private final LoadBalancer loadBalancer;
    private final ServiceRegistry serviceRegistry;
    private final SerializerType serializerType;

    public RpcClientFactory(LoadBalancer loadBalancer){
        String type = ApplicationConfig.getStringProperty("netty.registry.type");
        String addr = ApplicationConfig.getStringProperty("netty.registry.address");
        this.serviceRegistry = ServiceRegistryFactory.createServiceRegistry(type, addr);
        this.loadBalancer = Objects.requireNonNull(loadBalancer);

        this.serializerType = ApplicationConfigUtil.getSerializeTypeFromConfigFile();
    }

    public RpcClient<RpcRequest, RpcResponse> createRpcClient() {

        if(serializerType.equals(SerializerType.JSON)){
            return new JsonRpcClient(serviceRegistry, loadBalancer);
        } else if(serializerType.equals(SerializerType.KYRO)){
            return new KryoRpcClient(serviceRegistry, loadBalancer);
        }else if(serializerType.equals(SerializerType.PROTOBUF)){
            return new ProtobufRpcClient(serviceRegistry, loadBalancer);
        }else if(serializerType.equals(SerializerType.JDK)){
            return new JdkSerializerRpcClient(serviceRegistry, loadBalancer);
        }else if(serializerType.equals(SerializerType.FST)){
            return new FstRpcClient(serviceRegistry, loadBalancer);
        }else if(serializerType.equals(SerializerType.FLATBUFFERS)){
            return new FlatBuffersRpcClient(serviceRegistry, loadBalancer);
        }else if(serializerType.equals(SerializerType.HESSIAN)){
            return new HessianRpcClient(serviceRegistry, loadBalancer);
        }else if(serializerType.equals(SerializerType.THRIFT)){
            return new ThriftRpcClient(serviceRegistry, loadBalancer);
        }else if(serializerType.equals(SerializerType.MESSAGEPACK)){
            return new MessagePackRpcClient(serviceRegistry, loadBalancer);
        }
        throw new RuntimeException("Unsupported serializer type: " + serializerType);
    }
}
