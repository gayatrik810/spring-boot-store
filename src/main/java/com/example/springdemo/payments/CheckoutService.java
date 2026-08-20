package com.example.springdemo.payments;

import com.example.springdemo.entities.Cart;
import com.example.springdemo.entities.Order;
import com.example.springdemo.exception.CartEmptyException;
import com.example.springdemo.exception.CartNotFoundException;
import com.example.springdemo.repositories.CartRepository;
import com.example.springdemo.repositories.OrderRepository;
import com.example.springdemo.services.AuthService;
import com.example.springdemo.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final AuthService authService;
    private final PaymentGateway paymentGateway;

    @Transactional
    public CheckoutResponse checkout(
            CheckoutRequest request) {

        // 1. Get cart with items
        Cart cart = cartRepository
                .getCartWithItems(request.getCartId())
                .orElseThrow(
                        CartNotFoundException::new
                );

        // 2. Check whether cart is empty
        if (cart.getCartItems() == null ||
                cart.getCartItems().isEmpty()) {

            throw new CartEmptyException();
        }

        // 3. Get currently logged-in user
        var currentUser =
                authService.getCurrentUser();

        // 4. Create order from cart
        Order order = Order.fromCart(
                cart,
                currentUser
        );

        // 5. Save order
        orderRepository.save(order);

        try {

            // 6. Create Stripe checkout session
            CheckoutSession session =
                    paymentGateway
                            .createCheckoutSession(order);

            // 7. Clear cart after Stripe succeeds
            cartService.clearCart(
                    cart.getId()
            );

            // 8. Return checkout response
            return new CheckoutResponse(
                    order.getId(),
                    session.url()
            );

        } catch (PaymentException ex) {

            // Stripe failed -> remove order
            orderRepository.delete(order);

            throw ex;
        }
    }

    public void handleWebhookEvent(WebhookRequest request)
    {
        paymentGateway.parseWebhookRequest(request)
                .ifPresent( paymentResult-> {
                    var order =  orderRepository.findById(paymentResult.getOrderId()).orElseThrow();
                    order.setStatus(paymentResult.getPaymentStatus());
                    orderRepository.save(order);

    });
        // update order status

    }
}