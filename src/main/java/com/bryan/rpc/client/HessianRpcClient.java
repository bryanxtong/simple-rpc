package com.bryan.rpc.client;

import com.bryan.rpc.client.handler.HessianRpcClientHandler;
import com.bryan.rpc.common.codec.HessianDecoder;
import com.bryan.rpc.common.codec.HessianEncoder;
import com.bryan.rpc.client.loadbalance.LoadBalancer;
import com.bryan.rpc.registry.ServiceRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
public class HessianRpcClient extends CommonRpcClient{
    public HessianRpcClient(ServiceRegistry registry, LoadBalancer loadBalancer) {
        super(registry, loadBalancer);
    }

    @Override
    protected ChannelHandler createRpcClientHandler() {
        return new HessianRpcClientHandler();
    }

    @Override
    protected void addSerializers(ChannelPipeline pipeline) {
        pipeline.addLast(new HessianDecoder())
                .addLast(new HessianEncoder());
    }
}
