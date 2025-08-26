package com.bryan.rpc.example.service;

import com.bryan.rpc.server.RpcServer;
import com.bryan.rpc.server.RpcServerBootstrap;
//--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.math=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.sql/java.sql=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED
//scan the netty rpc servers packages
@RpcServer(scanBasePackages = {"com.bryan.rpc.example.service"})
public class NettyExampleServerApp {
    public static void main(String[] args) throws Exception {
        RpcServerBootstrap bootstrap = new RpcServerBootstrap(NettyExampleServerApp.class);
        bootstrap.start();
        bootstrap.awaitTermination();
    }
}
