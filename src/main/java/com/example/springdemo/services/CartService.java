package com.example.springdemo.services;

import com.example.springdemo.dtos.CartDto;
import com.example.springdemo.dtos.CartItemDto;
import com.example.springdemo.entities.Cart;
import com.example.springdemo.exception.CartNotFoundException;
import com.example.springdemo.exception.ProductNotFoundException;
import com.example.springdemo.mappers.CartMapper;
import com.example.springdemo.repositories.CartRepository;
import com.example.springdemo.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    public CartService(
            ProductRepository productRepository,
            CartRepository cartRepository,
            CartMapper cartMapper) {

        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
    }

    public CartDto createCart() {

        Cart cart = new Cart();

        cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }

    public CartItemDto addIToCart(UUID cartId, Long productId) {

        var cart = cartRepository
                .getCartWithItems(cartId)
                .orElse(null);

        if (cart == null) {
            throw new CartNotFoundException();
        }

        var product = productRepository
                .findById(productId)
                .orElse(null);

        if (product == null) {
            throw new ProductNotFoundException();
        }

        var cartItem = cart.addItem(product);

        cartRepository.save(cart);

        return cartMapper.toDto(cartItem);
    }

    public void clearCart(UUID cartId) {

        Cart cart = cartRepository
                .findById(cartId)
                .orElseThrow(CartNotFoundException::new);

        cart.clear();

        cartRepository.save(cart);
    }
}