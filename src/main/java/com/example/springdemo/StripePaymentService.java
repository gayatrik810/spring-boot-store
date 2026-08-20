package com.example.springdemo;

public class StripePaymentService implements PaymentService {
    @Override
    public void processPayment(double amount)
    {
        System.out.println("Stripe");
        System.out.println("Amount " + amount + "paid");
    }
}
