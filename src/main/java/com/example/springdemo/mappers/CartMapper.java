package com.example.springdemo.mappers;

import com.example.springdemo.dtos.CartDto;
import com.example.springdemo.dtos.CartItemDto;
import com.example.springdemo.entities.Cart;
import com.example.springdemo.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "items", source = "cartItems")
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toDto(CartItem cartItem);
}