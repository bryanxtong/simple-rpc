package com.bryan.rpc.example.service;


import com.bryan.rpc.server.RpcService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@RpcService(serviceName = "com.bryan.rpc.example.service.HelloService")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name, Instant instant) {
        return "Welcome to RPC World!!! " + name + " @ " + instant.toString();
    }

    @Override
    public String sayHello(String name, LocalDateTime instant) {
        return "Welcome to RPC World!!! " + name + " @ " + instant.toString();
    }


    @Override
    public String sayHello(String name) {
        return "Welcome to RPC World!!! " + name;
    }

    @Override
    public int sum(int a, int b) {
        return a + b;
    }


    public int sum(Integer a, Integer b) {
        return a + b + 10000;
    }

    @Override
    public Integer sumBox(Integer a, Integer b) {
        return a + b + 1;
    }


    @Override
    public CompletableFuture<String> sayHelloAsync(String name, Instant instant) {
        return CompletableFuture.supplyAsync(() -> "Welcome to RPC World!!! " + name + " @ " + instant.toString());
    }

    @Override
    public void print(int a, int b) {
        System.out.println(a + b);
    }

    @Override
    public CompletableFuture<Void> print(Integer a, Integer b) {
        return CompletableFuture.runAsync(() -> {
            System.out.println(a + b);
        });
    }

    @Override
    public void print(String name, LocalDateTime dateTime) {
        System.out.println(name + "is working at: " + dateTime);
    }

    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        CompletableFuture<String> hello = helloService.sayHelloAsync("hello", Instant.now());
        hello.whenComplete((s, throwable) -> {
            if (throwable == null) {
                System.out.println(s);
            }
        });

        hello.join();
    }
}
