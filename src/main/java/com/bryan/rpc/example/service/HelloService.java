package com.bryan.rpc.example.service;

import com.bryan.rpc.client.RpcServiceReference;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@RpcServiceReference(loadbalance = "random")
public interface HelloService {

    String sayHello(String name);

    String sayHello(String name, LocalDateTime dateTime);

    String sayHello(String name, Instant time);

    public int sum(int a, int b);
    public int sum(Integer a, Integer b);

    public Integer sumBox(Integer a,Integer b);

    CompletableFuture<String> sayHelloAsync(String name, Instant time);

    public void print(int a, int b);

    public CompletableFuture<Void> printAsync(Integer a, Integer b);

    public void print(String name, LocalDateTime dateTime);
}
