package com.example.springdemo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "orders", schema = "store1")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(
            name = "created_at",
            insertable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @OneToMany(
            mappedBy = "order",
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.REMOVE
            }
    )
    private Set<OrderItem> items =
            new LinkedHashSet<>();


    public static Order fromCart(
            Cart cart,
            User user) {

        Order order = new Order();

        order.setCustomer(user);

        // IMPORTANT
        order.setStatus(PaymentStatus.PENDING);

        order.setTotalPrice(
                cart.getTotalPrice()
        );

        for (CartItem cartItem :
                cart.getCartItems()) {

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrder(order);

            orderItem.setProduct(
                    cartItem.getProduct()
            );

            orderItem.setQuantity(
                    cartItem.getQuantity()
            );

            orderItem.setTotalPrice(
                    cartItem.getTotalPrice()
            );

            BigDecimal unitPrice =
                    cartItem
                            .getTotalPrice()
                            .divide(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    ),
                                    2,
                                    RoundingMode.HALF_UP
                            );

            orderItem.setUnitPrice(unitPrice);

            order.getItems().add(orderItem);
        }

        return order;
    }
}