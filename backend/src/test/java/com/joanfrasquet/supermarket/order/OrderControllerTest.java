package com.joanfrasquet.supermarket.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService service;

    @Test
    void createsOrder() throws Exception {
        when(service.create(any())).thenReturn(new OrderResponse(
                1L, Instant.parse("2026-10-07T10:00:00Z"), new BigDecimal("2.85"),
                List.of(new OrderResponse.Line(1L, "Milk 1L", 3, new BigDecimal("0.95"), new BigDecimal("2.85")))));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"lines": [{"productId": 1, "quantity": 3}]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(2.85))
                .andExpect(jsonPath("$.lines[0].productName").value("Milk 1L"));
    }

    @Test
    void rejectsOrderWithoutLines() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"lines": []}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void rejectsZeroQuantity() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"lines": [{"productId": 1, "quantity": 0}]}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void returns409WhenThereIsNotEnoughStock() throws Exception {
        when(service.create(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Not enough stock for product 1"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"lines": [{"productId": 1, "quantity": 500}]}
                                """))
                .andExpect(status().isConflict());
    }
}
