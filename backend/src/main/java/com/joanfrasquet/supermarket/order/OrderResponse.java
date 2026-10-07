package com.joanfrasquet.supermarket.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * What the API returns for an order. Built from the entity inside the transaction,
 * so the JSON never depends on lazy-loaded data and never exposes more than these fields.
 */
public record OrderResponse(Long id, Instant createdAt, BigDecimal total, List<Line> lines) {

    public record Line(Long productId, String productName, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
    }

    static OrderResponse from(CustomerOrder order) {
        List<Line> lines = order.getLines().stream()
                .map(line -> new Line(
                        line.getProduct().getId(),
                        line.getProduct().getName(),
                        line.getQuantity(),
                        line.getUnitPrice(),
                        line.getSubtotal()))
                .toList();
        return new OrderResponse(order.getId(), order.getCreatedAt(), order.getTotal(), lines);
    }
}
