package com.cashinvoice.controller;

import com.cashinvoice.model.CreateOrderRequest;
import com.cashinvoice.model.Order;
import com.cashinvoice.model.OrderResponse;
import com.cashinvoice.service.OrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Received request to create order for customer: {}", request.getCustomerId());

        OrderResponse response = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        log.info("Received request to fetch order with ID: {}", orderId);

        Order order = orderService.getOrderById(orderId);

        return ResponseEntity.ok(order);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@RequestParam String customerId) {
        log.info("Received request to fetch orders for customer: {}", customerId);

        List<Order> orders = orderService.getOrdersByCustomerId(customerId);

        return ResponseEntity.ok(orders);
    }



    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("Received request to fetch all orders");

        List<Order> orders = orderService.getAllOrders();

        return ResponseEntity.ok(orders);
    }
    @PutMapping("/{id}")
        public  ResponseEntity<Order> updateOrder(
                @PathVariable Long id ,
                @RequestBody Order order) {
            return ResponseEntity.ok(orderService.updateOrder(id, order));
        }