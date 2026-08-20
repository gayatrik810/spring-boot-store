package com.example.springdemo.controllers;

import com.example.springdemo.dtos.AddItemToCartRequest;
import com.example.springdemo.dtos.CartDto;
import com.example.springdemo.dtos.CartItemDto;
import com.example.springdemo.dtos.UpdateCartItemRequest;
import com.example.springdemo.entities.CartItem;
import com.example.springdemo.exception.CartNotFoundException;
import com.example.springdemo.exception.ProductNotFoundException;
import com.example.springdemo.mappers.CartMapper;
import com.example.springdemo.repositories.CartRepository;
import com.example.springdemo.repositories.ProductRepository;
import com.example.springdemo.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;
    private final CartService cartService;

    // CREATE CART
    @PostMapping
    public ResponseEntity<CartDto> createCart(
            UriComponentsBuilder uriBuilder) {

        var cartDto = cartService.createCart();

        var uri = uriBuilder
                .path("/carts/{id}")
                .buildAndExpand(cartDto.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(cartDto);
    }

    // GET CART
    @GetMapping("/{id}")
    public ResponseEntity<CartDto> getCart(
            @PathVariable UUID id) {

        var cart = cartRepository
                .getCartWithItems(id)
                .orElse(null);

        if (cart == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity
                .ok(cartMapper.toDto(cart));
    }

    // UPDATE CART ITEM
    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> updateItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId") Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        var cart = cartRepository
                .getCartWithItems(cartId)
                .orElse(null);

        if (cart == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "cart not found."
                    ));
        }

        CartItem cartItem = cart.getItem(productId);

        if (cartItem == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "product not found in the cart."
                    ));
        }

        cartItem.setQuantity(request.getQuantity());

        cartRepository.save(cart);

        return ResponseEntity
                .ok(cartMapper.toDto(cartItem));
    }

    // REMOVE ITEM FROM CART
    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> removeItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId") Long productId) {

        var cart = cartRepository
                .getCartWithItems(cartId)
                .orElse(null);

        if (cart == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "cart not found."
                    ));
        }

        cart.removeItem(productId);

        cartRepository.save(cart);

        return ResponseEntity
                .noContent()
                .build();
    }

    // CLEAR CART
    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> clearCart(
            @PathVariable UUID cartId) {

        var cart = cartRepository
                .getCartWithItems(cartId)
                .orElse(null);

        if (cart == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        cart.clear();

        cartRepository.save(cart);

        return ResponseEntity
                .noContent()
                .build();
    }

    // ADD ITEM TO CART
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addToCart(
            @PathVariable UUID cartId,
            @RequestBody AddItemToCartRequest request) {

        var cartItemDto = cartService.addIToCart(
                cartId,
                request.getProductId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cartItemDto);
    }

    // CART NOT FOUND EXCEPTION
    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCartNotFound() {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "cart not found."
                ));
    }

    // PRODUCT NOT FOUND EXCEPTION
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFound() {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "product not found."
                ));
    }
}