package com.bryan.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Non-IDL and IDL Rpc clients
 * @param <TRequest>
 * @param <TResponse>
 */
public abstract class AbstractRpcClient<TRequest,TResponse> implements RpcClient<TRequest,TResponse> {
    protected Channel channel;
    protected final EventLoopGroup group = new NioEventLoopGroup();

    protected Channel createChannel(String host, int port) throws InterruptedException {
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                        addSerializers(pipeline);
                        pipeline.addLast(createRpcClientHandler());
                    }
                });
        ChannelFuture f = b.connect(host, port).sync();
        channel = f.channel();
        return channel;
    }

    protected abstract ChannelHandler createRpcClientHandler();

    protected abstract void addSerializers(ChannelPipeline pipeline);

    public void shutdown() {
        if (channel != null && channel.isActive()) {
            channel.close();
        }
        group.shutdownGracefully();
    }

}
