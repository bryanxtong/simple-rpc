package com.bryan.rpc.client;

import com.bryan.rpc.client.handler.KyroRpcClientHandler;
import com.bryan.rpc.client.loadbalance.LoadBalancer;
import com.bryan.rpc.common.codec.KryoDecoder;
import com.bryan.rpc.common.codec.KryoEncoder;
import com.bryan.rpc.registry.ServiceRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
public class KryoRpcClient extends CommonRpcClient {
    public KryoRpcClient(ServiceRegistry registry, LoadBalancer loadBalancer) {
        super(registry, loadBalancer);
    }

    @Override
    protected ChannelHandler createRpcClientHandler() {
        return new KyroRpcClientHandler();
    }

    @Override
    protected void addSerializers(ChannelPipeline pipeline) {
        pipeline.addLast(new KryoDecoder())
                .addLast(new KryoEncoder());
    }
}
