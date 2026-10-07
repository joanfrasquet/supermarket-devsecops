package com.joanfrasquet.supermarket.order;

import com.joanfrasquet.supermarket.product.Product;
import com.joanfrasquet.supermarket.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-07T10:00:00Z");

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private OrderService service;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        productRepository = mock(ProductRepository.class);
        service = new OrderService(orderRepository, productRepository, Clock.fixed(NOW, ZoneOffset.UTC));
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createsOrderAndTakesUnitsOutOfStock() {
        Product milk = new Product("Milk 1L", new BigDecimal("0.95"), 10, "Dairy");
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(milk));

        OrderResponse order = service.create(new OrderRequest(List.of(new OrderRequest.Line(1L, 3))));

        assertThat(milk.getStock()).isEqualTo(7);
        assertThat(order.total()).isEqualByComparingTo("2.85");
        assertThat(order.createdAt()).isEqualTo(NOW);
        assertThat(order.lines()).hasSize(1);
    }

    @Test
    void rejectsOrderWithoutEnoughStock() {
        Product milk = new Product("Milk 1L", new BigDecimal("0.95"), 2, "Dairy");
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(milk));

        assertThatThrownBy(() -> service.create(new OrderRequest(List.of(new OrderRequest.Line(1L, 3)))))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        assertThat(milk.getStock()).isEqualTo(2);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void rejectsOrderForUnknownProduct() {
        when(productRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(new OrderRequest(List.of(new OrderRequest.Line(99L, 1)))))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
