package com.nus.shoppingcart.repository;

import com.nus.shoppingcart.model.Order;
import com.nus.shoppingcart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
