simple-rpc is a simple rpc program which is to illustrate the usage of different encoding/decoding format integration, like Json, flatbuffers,thrift,protobuf,kyro, messagepack,jdk,fst and hessian2

# Functions Supported
```
1. It supports sync call and async call with CompletableFuture

2. It supports encoding/decoding format like Json,flatbuffers,thrift,protobuf,kyro, 
messagepack,jdk,fst and hessian2 but It only supports one way at one time via 
the application.yml config file.

3.Flatbuffers/thrift/protobuf are based on IDL, and others are based on Non-IDL

4.For idl generated code, the dynamic field, for example, Response.result, 
Thrift/flatbuffers is using jackson-dataformat-msgpack and protobuf is using jackson

5.It will use application.yml for user application, and will override the setting 
in config.yml which is used default by the framework.
``` 
# How to Use
1. use a zk as a service registry
2. you need to add the following before you continue as serialization library hessian2/fst etc. need it
```
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.math=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
--add-opens java.base/java.text=ALL-UNNAMED
--add-opens java.sql/java.sql=ALL-UNNAMED 
--add-opens java.base/java.util=ALL-UNNAMED
--add-opens java.base/java.net=ALL-UNNAMED
--add-opens java.base/java.time=ALL-UNNAMED

```
netty server side needs to specify the backages of rpc services to scan
```
package com.bryan.rpc.example.service;

import com.bryan.rpc.server.RpcServer;
import com.bryan.rpc.server.RpcServerBootstrap;
@RpcServer(scanBasePackages = {"com.bryan.rpc.example.service"})
public class NettyExampleServerApp {
    public static void main(String[] args) throws Exception {
        RpcServerBootstrap bootstrap = new RpcServerBootstrap(NettyExampleServerApp.class);
        bootstrap.start();
        bootstrap.awaitTermination();
    }
}
```
netty server clientside needs to close the rpcclient as we have hidden it behind the proxy factory
```
package com.bryan.rpc.example.service;
import com.bryan.rpc.client.proxy.RpcProxyFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NettyExampleClient {

    public static void main(String[] args) throws Exception {

        try {
            RpcProxyFactory factory = new RpcProxyFactory();
            HelloService helloService = factory.getProxy(HelloService.class);
            int sum = helloService.sum(233, 455);
            System.out.println(sum);
            
            CompletableFuture<String> helloAsync = helloService.sayHelloAsync("Bryan", Instant.now());
            helloAsync.thenAccept(System.out::println);
            helloAsync.join();
            //factory.close();

            PersonService proxy = factory.getProxy(PersonService.class);
            Person person = proxy.getPerson("Bryan", 100);
            System.out.println(person);

            CompletableFuture<Void> voidResultFuture = proxy.getPersonAsync("Bryan", 56565).thenAccept(System.out::println);
            voidResultFuture.join();
            factory.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
