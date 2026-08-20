package com.example.springdemo.repositories;

import com.example.springdemo.entities.Category;
import com.example.springdemo.entities.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductCriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Product> findProductsByCategory(Category category) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Product> criteriaQuery =
                criteriaBuilder.createQuery(Product.class);

        Root<Product> product = criteriaQuery.from(Product.class);

        criteriaQuery.select(product)
                .where(criteriaBuilder.equal(product.get("category"), category));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}