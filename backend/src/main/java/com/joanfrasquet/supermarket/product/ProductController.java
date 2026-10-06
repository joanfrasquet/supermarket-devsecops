package com.joanfrasquet.supermarket.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> findAll() {
        return service.findAll();
    }

    // TODO(Joan): GET /{id}, POST, PUT /{id} and DELETE /{id}.
    // Use @Valid on request bodies and return the right status codes (201, 204, 404).
}
