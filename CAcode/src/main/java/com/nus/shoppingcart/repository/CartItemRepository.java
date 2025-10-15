package com.nus.shoppingcart.repository;

import com.nus.shoppingcart.model.CartItem;
import com.nus.shoppingcart.model.User;
import com.nus.shoppingcart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    
    // 添加这个方法：查找特定用户的特定商品
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    
    @Transactional
    void deleteByUser(User user);
}
