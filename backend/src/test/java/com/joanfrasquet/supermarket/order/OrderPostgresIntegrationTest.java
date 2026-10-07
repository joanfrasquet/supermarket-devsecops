package com.joanfrasquet.supermarket.order;

import com.joanfrasquet.supermarket.product.Product;
import com.joanfrasquet.supermarket.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Runs against a real PostgreSQL started in Docker, the same version as docker-compose.yml.
 * Skipped when Docker is not available; it always runs in CI.
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class OrderPostgresIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void failedOrderLeavesStockAndOrdersUnchanged() {
        Product rice = productRepository.save(new Product("Rice 1kg", new BigDecimal("1.50"), 5, "Groceries"));
        Product oil = productRepository.save(new Product("Olive oil 1L", new BigDecimal("8.90"), 1, "Groceries"));
        long ordersBefore = orderRepository.count();

        // The first line fits in stock, the second does not: the whole order must be rolled back.
        OrderRequest request = new OrderRequest(List.of(
                new OrderRequest.Line(rice.getId(), 3),
                new OrderRequest.Line(oil.getId(), 2)));

        assertThatThrownBy(() -> orderService.create(request)).isInstanceOf(ResponseStatusException.class);

        assertThat(productRepository.findById(rice.getId()).orElseThrow().getStock()).isEqualTo(5);
        assertThat(productRepository.findById(oil.getId()).orElseThrow().getStock()).isEqualTo(1);
        assertThat(orderRepository.count()).isEqualTo(ordersBefore);
    }

    @Test
    void savedOrderCanBeReadBack() {
        Product rice = productRepository.save(new Product("Rice 1kg", new BigDecimal("1.50"), 5, "Groceries"));

        OrderResponse created = orderService.create(new OrderRequest(List.of(new OrderRequest.Line(rice.getId(), 2))));

        OrderResponse loaded = orderService.findById(created.id());
        assertThat(loaded.total()).isEqualByComparingTo("3.00");
        assertThat(loaded.lines()).extracting(OrderResponse.Line::productName).containsExactly("Rice 1kg");
        assertThat(productRepository.findById(rice.getId()).orElseThrow().getStock()).isEqualTo(3);
    }
}
