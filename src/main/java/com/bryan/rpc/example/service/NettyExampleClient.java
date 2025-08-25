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

            Integer a =233; Integer b = 455;
            int sum2 = helloService.sum(a, b);
            System.out.println(sum2);

            helloService.print(122,4545);
            CompletableFuture<Void> print = helloService.print(a, b);
            print.join();
            Integer sum3 = helloService.sumBox(a, b);
            System.out.println(sum3);
            CompletableFuture<String> helloAsync = helloService.sayHelloAsync("Bryan", Instant.now());
            helloAsync.thenAccept(System.out::println);

            helloAsync.join();
            //factory.close();

            PersonService proxy = factory.getProxy(PersonService.class);
            Person person = proxy.getPerson("Bryan", 100);
            System.out.println(person);

            Person person2 = new Person("bryan",34,"shanghai", Instant.now());
            Person person3 = new Person("jimmy",34,"shanghai", Instant.now());
            Person person4 = new Person("kate",34,"shanghai", Instant.now());
            Person person5 = new Person("raimond",34,"shanghai", Instant.now());

            Map<String,Person> maps = new HashMap<>();
            maps.put(person4.name,person4);
            maps.put(person5.name,person5);
            List<Person> personList = new ArrayList<>();
            personList.add(person2);
            personList.add(person3);
            List<Person> personList1 = proxy.getPersonList(personList, maps);
            System.out.println(personList1);

            Map<String, Person> personMap = proxy.getPersonMap(personList, maps);
            System.out.println(personMap);

            CompletableFuture<Void> voidResultFuture = proxy.getPersonAsync("Bryan", 56565).thenAccept(System.out::println);
            voidResultFuture.join();
            factory.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

