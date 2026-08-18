package com.orderservice.orderservice.service;

import com.orderservice.orderservice.model.Order;
import com.orderservice.orderservice.model.Product;
import com.orderservice.orderservice.repository.OrderRepository;
import com.orderservice.orderservice.repository.ProductRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public Order placeOrder(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);

        try {
            productRepository.save(product);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException("Product was updated by someone else, please retry");
        }

        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setStatus("PLACED");

        return orderRepository.save(order);
    }
}