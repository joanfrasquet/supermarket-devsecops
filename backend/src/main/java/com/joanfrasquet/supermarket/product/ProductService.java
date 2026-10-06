package com.joanfrasquet.supermarket.product;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    // TODO(Joan): findById, create, update and delete.
    // Throw a clear exception when a product does not exist so the controller can return 404.
}
