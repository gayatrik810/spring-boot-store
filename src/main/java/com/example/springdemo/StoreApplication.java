package com.example.springdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

public class StoreApplication {

    public static void main(String[] args)
    {
        ApplicationContext context = SpringApplication.run(StoreApplication.class);
                var manager = context.getBean(NotificationManager.class);
                manager.sendNotification("This is a test");
    }
}
