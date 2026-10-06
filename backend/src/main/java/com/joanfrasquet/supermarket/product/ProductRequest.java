package com.joanfrasquet.supermarket.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Data a client can send to create or update a product.
 * It has no id on purpose: the database assigns it, so a client cannot overwrite another product.
 */
public record ProductRequest(
        @NotBlank String name,
        @NotNull @DecimalMin("0.00") BigDecimal price,
        @Min(0) int stock,
        @NotBlank String category
) {
}
