package com.bryan.rpc.example.service;

import com.bryan.rpc.server.RpcService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RpcService(serviceName = "com.bryan.rpc.example.service.PersonService")
public class PersonServiceImpl implements PersonService {
    @Override
    public Person getPerson(String name, Integer age) {
        return new Person(name,age,"shanghai", Instant.now());
    }

    @Override
    public CompletableFuture<Person> getPersonAsync(String name, Integer age) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Person(name,age,"shanghai", Instant.now());
        });
    }

    @Override
    public Map<String, Person> getPersonMap(List<Person> persons, Map<String, Person> maps) {
        Map<String, Person> stringPersonMap = new HashMap<>();
        stringPersonMap.putAll(maps);
        for(Person person : persons){
            stringPersonMap.put(person.name, person);
        }

        return stringPersonMap;
    }

    @Override
    public List<Person> getPersonList(List<Person> persons, Map<String, Person> maps) {
        List<Person> personList = new ArrayList<>();
        personList.addAll(persons);
        maps.forEach((k,v)->{
            personList.add(v);
        });

        return personList;
    }
}
