package com.example.springdemo.repositories;

import com.example.springdemo.entities.Category;
import com.example.springdemo.entities.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpec {

    public static Specification<Product> hasCategory(Category category) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category"), category);

    }
}