package com.cashinvoice.service;

import com.cashinvoice.exception.OrderNotFoundException;
import com.cashinvoice.model.CreateOrderRequest;
import com.cashinvoice.model.Order;
import com.cashinvoice.model.OrderResponse;
import com.cashinvoice.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${file.input.path:input/orders}")
    private String inputPath;

    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());

        validateOrderRequest(request);

        String orderId = UUID.randomUUID().toString();
        LocalDateTime createdAt = LocalDateTime.now();

        Order order = new Order(
                orderId,
                request.getCustomerId(),
                request.getProduct(),
                request.getAmount(),
                createdAt
        );

        orderRepository.save(order);

        log.info("Order created successfully with ID: {}", orderId);

        writeOrderToFile(order);

        return new OrderResponse(orderId, "CREATED");
    }

    public Order getOrderById(String orderId) {
        log.info("Fetching order with ID: {}", orderId);

        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
    }

    public List<Order> getOrdersByCustomerId(String customerId) {
        log.info("Fetching orders for customer: {}", customerId);

        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll();
    }

    private void validateOrderRequest(CreateOrderRequest request) {
        if (request.getCustomerId() == null || request.getCustomerId().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID must not be null or empty");
        }

        if (request.getProduct() == null || request.getProduct().trim().isEmpty()) {
            throw new IllegalArgumentException("Product must not be null or empty");
        }

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
    }

    private void writeOrderToFile(Order order) {
        try {
            File directory = new File(inputPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = String.format("order-%s.json", order.getOrderId());
            File file = new File(directory, fileName);

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, order);

            log.info("Order written to file: {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Error writing order to file: {}", e.getMessage(), e);
        }
    }


    public OrderRepository OrderRepository(Long orderId, order ordr) {

    }
}