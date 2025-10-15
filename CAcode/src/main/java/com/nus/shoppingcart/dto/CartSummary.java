package com.nus.shoppingcart.dto;

import com.nus.shoppingcart.model.CartItem;
import java.math.BigDecimal;
import java.util.List;

public class CartSummary {
    private List<CartItem> items;
    private BigDecimal totalPrice;
    private int totalItems;
    
    public CartSummary(List<CartItem> items, BigDecimal totalPrice, int totalItems) {
        this.items = items;
        this.totalPrice = totalPrice;
        this.totalItems = totalItems;
    }
    
    // Getters and Setters
    public List<CartItem> getItems() {
        return items;
    }
    
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public int getTotalItems() {
        return totalItems;
    }
    
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
}
