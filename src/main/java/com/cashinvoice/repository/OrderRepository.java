package com.cashinvoice.repository;

import com.cashinvoice.model.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class OrderRepository {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public Order save(Order order) {
        orders.put(order.getOrderId(), order);
        return order;
    }

    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    public List<Order> findByCustomerId(String customerId) {
        return orders.values().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public List<Order> findAll() {
        return orders.values().stream().collect(Collectors.toList());
    }

    public boolean existsById(String orderId) {
        return orders.containsKey(orderId);
    }

    public void deleteById(String orderId) {
        orders.remove(orderId);
    }

    public void clear() {
        orders.clear();
    }
}