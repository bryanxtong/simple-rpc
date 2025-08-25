package com.bryan.rpc.example.service;

import java.io.Serializable;
import java.time.Instant;

public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    public String name;
    public int age;
    public String address;
    public Instant birthday;
    public Person(){

    }

    public Person(String name, int age, String address, Instant birthday) {
        this.name = name;
        this.age = age;
        this.address = address;
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
