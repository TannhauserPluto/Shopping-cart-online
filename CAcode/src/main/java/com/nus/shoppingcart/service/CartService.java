package com.nus.shoppingcart.service;

import com.nus.shoppingcart.dto.CartSummary;
import com.nus.shoppingcart.exception.BadRequestException;
import com.nus.shoppingcart.exception.ResourceNotFoundException;
import com.nus.shoppingcart.model.CartItem;
import com.nus.shoppingcart.model.Product;
import com.nus.shoppingcart.model.User;
import com.nus.shoppingcart.repository.CartItemRepository;
import com.nus.shoppingcart.repository.ProductRepository;
import com.nus.shoppingcart.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartItemRepository cartRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    public CartService(CartItemRepository cartRepo, UserRepository userRepo, ProductRepository productRepo) {
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    public List<CartItem> getCartByUser(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return cartRepo.findByUser(user);
    }

    public CartSummary getCartSummary(Long userId) {
        List<CartItem> items = getCartByUser(userId);
        BigDecimal totalPrice = calculateTotalPrice(items);
        int totalItems = calculateTotalItems(items);
        
        return new CartSummary(items, totalPrice, totalItems);
    }
    
    private BigDecimal calculateTotalPrice(List<CartItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            BigDecimal price = BigDecimal.valueOf(item.getProduct().getPrice());
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal itemTotal = price.multiply(quantity);
            total = total.add(itemTotal);
        }
        return total;
    }
    
    private int calculateTotalItems(List<CartItem> items) {
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    @Transactional
    public CartItem addToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Product product = productRepo.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // 检查库存
        if (product.getStock() < quantity) {
            throw new BadRequestException("Insufficient stock. Available: " + product.getStock());
        }

        Optional<CartItem> existingItem = cartRepo.findByUserAndProduct(user, product);
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            
            // 再次检查总量是否超过库存
            if (product.getStock() < newQuantity) {
                throw new BadRequestException("Total quantity exceeds available stock");
            }
            
            item.setQuantity(newQuantity);
            return cartRepo.save(item);
        } else {
            CartItem item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(quantity);
            return cartRepo.save(item);
        }
    }

    @Transactional
    public CartItem updateCartItemQuantity(Long userId, Long productId, int quantity) {
        if (quantity < 0) {
            throw new BadRequestException("Quantity cannot be negative");
        }

        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Product product = productRepo.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (quantity > product.getStock()) {
            throw new BadRequestException("Quantity exceeds available stock");
        }

        Optional<CartItem> existingItem = cartRepo.findByUserAndProduct(user, product);
        
        if (existingItem.isPresent()) {
            if (quantity == 0) {
                cartRepo.delete(existingItem.get());
                return null;
            }
            CartItem item = existingItem.get();
            item.setQuantity(quantity);
            return cartRepo.save(item);
        } else {
            if (quantity == 0) {
                return null;
            }
            CartItem item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(quantity);
            return cartRepo.save(item);
        }
    }

    @Transactional
    public void removeFromCart(Long itemId) {
        if (!cartRepo.existsById(itemId)) {
            throw new ResourceNotFoundException("CartItem", "id", itemId);
        }
        cartRepo.deleteById(itemId);
    }

    @Transactional
    public void clearCart(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        cartRepo.deleteByUser(user);
    }
}
