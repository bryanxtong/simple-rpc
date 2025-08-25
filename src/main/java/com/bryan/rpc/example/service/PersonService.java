package com.bryan.rpc.example.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public interface PersonService {
    public Person getPerson(String name, Integer age);

    public CompletableFuture<Person> getPersonAsync(String name, Integer age);

    public Map<String,Person> getPersonMap(List<Person> persons, Map<String,Person> maps);

    public List<Person> getPersonList(List<Person> persons, Map<String,Person> maps);
}
