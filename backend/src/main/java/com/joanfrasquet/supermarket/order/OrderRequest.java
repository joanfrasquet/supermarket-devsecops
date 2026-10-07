package com.joanfrasquet.supermarket.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Data a client sends to place an order: only product ids and quantities.
 * Prices and the total are calculated on the server, so a client cannot choose what it pays.
 */
public record OrderRequest(@NotEmpty List<@Valid @NotNull Line> lines) {

    public record Line(@NotNull Long productId, @Min(1) int quantity) {
    }
}
