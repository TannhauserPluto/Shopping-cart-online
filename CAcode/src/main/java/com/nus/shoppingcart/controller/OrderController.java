package com.nus.shoppingcart.controller;

import com.nus.shoppingcart.model.Order;
import com.nus.shoppingcart.service.OrderService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/checkout/{userId}")
    public Order checkout(@PathVariable Long userId) {
        return service.checkout(userId);
    }

    @GetMapping("/history/{userId}")
    public List<Order> getOrderHistory(@PathVariable Long userId) {
        return service.getOrderHistory(userId);
    }
}
