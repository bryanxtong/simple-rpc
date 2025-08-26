package com.bryan.rpc.server;
import com.bryan.rpc.common.codec.*;
import com.bryan.rpc.common.model.SerializerType;
import com.bryan.rpc.server.handler.*;
import com.bryan.rpc.common.utils.ApplicationConfigUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class NettyRpcServer {
    private static final EventExecutorGroup EVENT_EXECUTORS = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 2);
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    //serviceName and rpc service implementation mapping
    private ObjectServiceRegistry serviceRegistry;
    private Channel serverChannel;

    public NettyRpcServer(ObjectServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void start(String host,int port) throws Exception {
        SerializerType serializerType = ApplicationConfigUtil.getSerializeTypeFromConfigFile();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));

                            if(serializerType.equals(SerializerType.PROTOBUF)) {
                                pipeline.addLast(new ProtobufDecoder())
                                        .addLast(new ProtobufEncoder())
                                        .addLast(EVENT_EXECUTORS,new ProtobufRpcServerHandler(serviceRegistry));
                            }else if(serializerType.equals(SerializerType.THRIFT)){
                                pipeline.addLast(new ThriftDecoder())
                                        .addLast(new ThriftEncoder())
                                        .addLast(new ThriftRpcServerHandler(serviceRegistry));
                            }else if(serializerType.equals(SerializerType.FLATBUFFERS)){
                                pipeline.addLast(new FbsDecoder())
                                        .addLast(new FbsEncoder())
                                        .addLast(EVENT_EXECUTORS,new FbsRpcServerHandler(serviceRegistry));
                            }else if(serializerType.equals(SerializerType.MESSAGEPACK)){
                                //MessagePack serialization has some problem for java 8 datetime, use separate rpc handler
                                pipeline.addLast(new MessagePackJacksonDecoder())
                                        .addLast(new MessagePackJacksonEncoder())
                                        .addLast(EVENT_EXECUTORS,new JacksonRpcServerHandler(serviceRegistry));
                            }else if(serializerType.equals(SerializerType.KYRO)){
                                pipeline.addLast(new KryoDecoder())
                                        .addLast(new KryoEncoder())
                                        .addLast(EVENT_EXECUTORS,new RpcServerHandler(serviceRegistry));
                            }else if(serializerType.equals(SerializerType.JSON)){
                                //Jackson serialization has some problem for java 8 datetime, use separate rpc handler
                                pipeline.addLast(new JsonDecoder())
                                        .addLast(new JsonEncoder())
                                        .addLast(EVENT_EXECUTORS,new JacksonRpcServerHandler(serviceRegistry));

                            }else if(serializerType.equals(SerializerType.FST)){
                                pipeline.addLast(new FstDecoder())
                                        .addLast(new FstEncoder())
                                        .addLast(EVENT_EXECUTORS,new RpcServerHandler(serviceRegistry));

                            }else if(serializerType.equals(SerializerType.HESSIAN)){
                                pipeline.addLast(new HessianDecoder())
                                        .addLast(new HessianEncoder())
                                        .addLast(EVENT_EXECUTORS,new RpcServerHandler(serviceRegistry));

                            }else if(serializerType.equals(SerializerType.JDK)){
                                pipeline.addLast(new JdkSerializerDecoder())
                                        .addLast(new JdkSerializerEncoder())
                                        .addLast(EVENT_EXECUTORS,new RpcServerHandler(serviceRegistry));
                            }
                        }
                    });
            ChannelFuture f = bootstrap.bind(host,port).sync();
            serverChannel = f.channel();
            //f.channel().closeFuture().sync();
        } finally {
            //stop();
        }
    }

    public Channel getServerChannel() {
        return serverChannel;
    }

    public void stop(){
        if(serverChannel != null) {
            serverChannel.close();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
