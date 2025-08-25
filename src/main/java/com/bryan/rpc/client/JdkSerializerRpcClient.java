package com.bryan.rpc.client;

import com.bryan.rpc.client.handler.JdkSerializerRpcClientHandler;
import com.bryan.rpc.common.codec.JdkSerializerDecoder;
import com.bryan.rpc.common.codec.JdkSerializerEncoder;
import com.bryan.rpc.client.loadbalance.LoadBalancer;
import com.bryan.rpc.registry.ServiceRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

public class JdkSerializerRpcClient extends CommonRpcClient{

    public JdkSerializerRpcClient(ServiceRegistry registry, LoadBalancer loadBalancer) {
        super(registry, loadBalancer);
    }

    @Override
    protected ChannelHandler createRpcClientHandler() {
        return new JdkSerializerRpcClientHandler();
    }

    @Override
    protected void addSerializers(ChannelPipeline pipeline) {
        pipeline.addLast(new JdkSerializerDecoder())
                .addLast(new JdkSerializerEncoder());
    }
}
