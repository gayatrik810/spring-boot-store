package com.example.springdemo.dtos;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;


public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Byte categoryId
) {


}