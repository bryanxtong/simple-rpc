package com.bryan.rpc.client;
import com.bryan.rpc.client.handler.MessagePackRpcClientHandler;
import com.bryan.rpc.client.loadbalance.LoadBalancer;
import com.bryan.rpc.common.codec.MessagePackJacksonDecoder;
import com.bryan.rpc.common.codec.MessagePackJacksonEncoder;
import com.bryan.rpc.registry.ServiceRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
public class MessagePackRpcClient extends CommonRpcClient {

    public MessagePackRpcClient(ServiceRegistry registry, LoadBalancer loadBalancer) {
        super(registry, loadBalancer);
    }

    @Override
    protected ChannelHandler createRpcClientHandler() {
        return new MessagePackRpcClientHandler();
    }

    @Override
    protected void addSerializers(ChannelPipeline pipeline) {
        pipeline.addLast(new MessagePackJacksonDecoder())
                .addLast(new MessagePackJacksonEncoder());
    }
}
