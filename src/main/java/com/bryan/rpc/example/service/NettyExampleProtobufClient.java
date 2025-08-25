package com.bryan.rpc.example.service;
import com.bryan.rpc.client.proxy.RpcProxyFactory;
public class NettyExampleProtobufClient {
    public static void main(String[] args) throws Exception {
        try {
            RpcProxyFactory factory = new RpcProxyFactory();
            ProtobufPersonService proxy2 = factory.getProxy(ProtobufPersonService.class);
            PersonOuterClass.Person bryan2 = proxy2.getPerson("Bryan", 56);
            System.out.println(bryan2);
            factory.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

