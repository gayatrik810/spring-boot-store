package com.example.springdemo.payments;

import com.example.springdemo.entities.Order;
import com.example.springdemo.entities.OrderItem;
import com.example.springdemo.entities.PaymentStatus;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class StripePaymentGateway implements PaymentGateway {

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    @Override
    public CheckoutSession createCheckoutSession(Order order) {

        try {

            // Create Stripe checkout session builder
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success?orderId=" + order.getId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel")
                     .putMetadata("order_id" , order.getId().toString());

            // Add every order item to Stripe
            order.getItems().forEach(item -> {

                var lineItem = createLineItem(item);

                builder.addLineItem(lineItem);
            });

            // Create Stripe session
            Session session = Session.create(builder.build());

            // Return Stripe checkout URL
            return new CheckoutSession(session.getUrl());

        } catch (StripeException ex) {

            System.out.println(
                    "Stripe error: " + ex.getMessage()
            );

            throw new PaymentException(
                    ex.getMessage()
            );
        }
    }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
        try {
            var payload = request.getPayload();
            var signature = request.getHeaders().get("Stripe-Signature");
            var event = Webhook.constructEvent(payload , signature, webhookSecretKey);

            return switch (event.getType()) {
                case "payment_intent.succeeded" ->
                        Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.PAID));

                case "payment_intent.payment_failed" ->
                     Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.FAILED));

                default ->  Optional.empty();
            };


        } catch (SignatureVerificationException e) {
            throw new PaymentException("Invalid Exception");
        }
    }

    private Long extractOrderId(Event event) {
        var stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow(
                () -> new PaymentException("Could not deserialize Stripe event. Check the SDK and API version")
        );
        var paymentIntent = (PaymentIntent) stripeObject;
        return Long.valueOf(paymentIntent.getMetadata().get("order_id"));
    }

    private SessionCreateParams.LineItem createLineItem(
            OrderItem item) {

        return SessionCreateParams.LineItem.builder()

                .setQuantity(
                        Long.valueOf(item.getQuantity())
                )

                .setPriceData(
                        createPriceData(item)
                )

                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(
            OrderItem item) {

        return SessionCreateParams.LineItem.PriceData.builder()

                .setCurrency("usd")

                .setUnitAmount(
                        item.getUnitPrice()
                                .multiply(
                                        BigDecimal.valueOf(100)
                                )
                                .longValue()
                )

                .setProductData(
                        createProductData(item)
                )

                .build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData
    createProductData(OrderItem item) {

        return SessionCreateParams
                .LineItem
                .PriceData
                .ProductData
                .builder()

                .setName(
                        item.getProduct().getName()
                )

                .build();
    }
}