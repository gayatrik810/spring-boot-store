package com.example.springdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


public class Student {

    @Value("${student.name}")
    private String name;

    @Value("${student.age}")
    private int age;

    @Value("${student.city}")
    private String city;

    public void display() {
        System.out.println("Name : " + name);
        System.out.println("Age : " + age);
        System.out.println("City : " + city);
    }
}