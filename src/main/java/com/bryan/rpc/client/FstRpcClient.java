package com.bryan.rpc.client;

import com.bryan.rpc.common.codec.FstDecoder;
import com.bryan.rpc.common.codec.FstEncoder;
import com.bryan.rpc.client.handler.FstRpcClientHandler;
import com.bryan.rpc.client.loadbalance.LoadBalancer;
import com.bryan.rpc.registry.ServiceRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

public class FstRpcClient extends CommonRpcClient{
    public FstRpcClient(ServiceRegistry registry, LoadBalancer loadBalancer) {
        super(registry, loadBalancer);
    }

    @Override
    protected ChannelHandler createRpcClientHandler() {
        return new FstRpcClientHandler();
    }

    @Override
    protected void addSerializers(ChannelPipeline pipeline) {
        pipeline.addLast(new FstDecoder())
                .addLast(new FstEncoder());
    }
}
