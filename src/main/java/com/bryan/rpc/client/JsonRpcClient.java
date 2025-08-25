package com.bryan.rpc.client;

import com.bryan.rpc.client.handler.JsonRpcClientHandler;
import com.bryan.rpc.client.loadbalance.LoadBalancer;
import com.bryan.rpc.common.codec.JsonDecoder;
import com.bryan.rpc.common.codec.JsonEncoder;
import com.bryan.rpc.registry.ServiceRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
public class JsonRpcClient extends CommonRpcClient {

    public JsonRpcClient(ServiceRegistry registry, LoadBalancer loadBalancer) {
        super(registry, loadBalancer);
    }

    @Override
    protected ChannelHandler createRpcClientHandler() {
        return new JsonRpcClientHandler();
    }

    @Override
    protected void addSerializers(ChannelPipeline pipeline) {
        pipeline.addLast(new JsonDecoder())
                .addLast(new JsonEncoder());
    }
}
