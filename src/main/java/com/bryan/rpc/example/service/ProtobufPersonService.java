package com.bryan.rpc.example.service;

import java.util.concurrent.CompletableFuture;
public interface ProtobufPersonService {
    public PersonOuterClass.Person getPerson(String name, Integer age);

    public CompletableFuture<PersonOuterClass.Person> getPersonAsync(String name, Integer age);
}
