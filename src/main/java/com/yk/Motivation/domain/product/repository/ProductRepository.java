package com.yk.Motivation.domain.product.repository;

import com.yk.Motivation.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
