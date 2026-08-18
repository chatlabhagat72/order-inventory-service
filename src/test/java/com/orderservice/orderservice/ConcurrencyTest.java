package com.orderservice.orderservice;

import com.orderservice.orderservice.model.Product;
import com.orderservice.orderservice.repository.ProductRepository;
import com.orderservice.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    @Test
    public void onlyOneOrderShouldSucceedWhenStockIsOne() throws InterruptedException {
        Product product = new Product();
        product.setName("Limited Item");
        product.setPrice(999.0);
        product.setStockQuantity(1);
        product = productRepository.save(product);

        Long productId = product.getId();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        CountDownLatch latch = new CountDownLatch(2);

        Runnable task = () -> {
            try {
                orderService.placeOrder(productId, 1);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        latch.await();

        assertEquals(1, successCount.get(), "Exactly one order should succeed");
        assertEquals(1, failCount.get(), "Exactly one order should fail");
    }
}