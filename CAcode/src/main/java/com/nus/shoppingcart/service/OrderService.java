package com.nus.shoppingcart.service;

import com.nus.shoppingcart.model.*;
import com.nus.shoppingcart.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final CartItemRepository cartRepo;
    private final UserRepository userRepo;

    public OrderService(OrderRepository orderRepo, CartItemRepository cartRepo, UserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
    }

    @Transactional  // 添加事务注解，确保订单创建和购物车清空的原子性
    public Order checkout(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        List<CartItem> cartItems = cartRepo.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        // 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        // 创建订单项
        List<OrderItem> orderItems = cartItems.stream().map(cart -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(cart.getProduct());
            item.setQuantity(cart.getQuantity());
            item.setPrice(cart.getProduct().getPrice() * cart.getQuantity());
            return item;
        }).collect(Collectors.toList());

        // 计算总价
        double total = orderItems.stream()
            .mapToDouble(OrderItem::getPrice)
            .sum();
        
        order.setTotalPrice(total);
        order.setItems(orderItems);

        // 保存订单
        Order savedOrder = orderRepo.save(order);

        // 清空购物车 - 使用更具体的方法
        cartRepo.deleteByUser(user);  // 或者使用 cartRepo.deleteAll(cartItems)

        return savedOrder;
    }

    public List<Order> getOrderHistory(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return orderRepo.findByUser(user);
    }
}
