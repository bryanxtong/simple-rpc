package com.bryan.rpc.example.service;

import com.bryan.rpc.server.RpcService;

import java.util.concurrent.CompletableFuture;

@RpcService(serviceName = "com.bryan.rpc.example.service.ProtobufPersonService")
public class ProtobufPersonServiceImpl implements ProtobufPersonService {
    @Override
    public PersonOuterClass.Person getPerson(String name, Integer age) {
        return PersonOuterClass.Person.newBuilder().setName(name).setAge(age).build();
    }

    @Override
    public CompletableFuture<PersonOuterClass.Person> getPersonAsync(String name, Integer age) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return PersonOuterClass.Person.newBuilder().setName(name).setAge(age).build();
        });
    }
}
