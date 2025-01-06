package com.aivle.project.repository;

import com.aivle.project.entity.products.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<ProductsEntity, String> {
}
