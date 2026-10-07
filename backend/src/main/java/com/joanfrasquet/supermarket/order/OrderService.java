package com.joanfrasquet.supermarket.order;

import com.joanfrasquet.supermarket.product.Product;
import com.joanfrasquet.supermarket.product.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final Clock clock;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, Clock clock) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        return orderRepository.findById(id)
                .map(OrderResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    /**
     * Creates the order and takes the units out of stock.
     * Everything runs in one transaction: if one line fails, no stock changes and no order is saved.
     */
    @Transactional
    public OrderResponse create(OrderRequest request) {
        CustomerOrder order = new CustomerOrder(Instant.now(clock));
        for (OrderRequest.Line requested : request.lines()) {
            Product product = productRepository.findByIdForUpdate(requested.productId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Product " + requested.productId() + " not found"));
            if (product.getStock() < requested.quantity()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Not enough stock for product " + product.getId());
            }
            product.setStock(product.getStock() - requested.quantity());
            order.addLine(new OrderLine(product, requested.quantity()));
        }
        return OrderResponse.from(orderRepository.save(order));
    }
}
