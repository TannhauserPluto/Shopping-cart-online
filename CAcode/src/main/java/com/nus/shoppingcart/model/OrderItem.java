package com.nus.shoppingcart.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    private Integer quantity;
    
    private Double price;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}


// import jakarta.persistence.*;

// @Entity
// @Table(name = "order_item")
// public class OrderItem {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
    
//     @ManyToOne
//     @JoinColumn(name = "order_id", nullable = false)  // ✅ 这里会自动引用 orders 表
//     private Order order;
    
//     @ManyToOne
//     @JoinColumn(name = "product_id", nullable = false)
//     private Product product;
    
//     @Column(name = "quantity", nullable = false)
//     private Integer quantity;
    
//     @Column(name = "price", nullable = false)
//     private Double price;
    
//     // Constructors
//     public OrderItem() {}
    
//     public OrderItem(Order order, Product product, Integer quantity, Double price) {
//         this.order = order;
//         this.product = product;
//         this.quantity = quantity;
//         this.price = price;
//     }
    
//     // Getters and Setters
//     public Long getId() {
//         return id;
//     }
    
//     public void setId(Long id) {
//         this.id = id;
//     }
    
//     public Order getOrder() {
//         return order;
//     }
    
//     public void setOrder(Order order) {
//         this.order = order;
//     }
    
//     public Product getProduct() {
//         return product;
//     }
    
//     public void setProduct(Product product) {
//         this.product = product;
//     }
    
//     public Integer getQuantity() {
//         return quantity;
//     }
    
//     public void setQuantity(Integer quantity) {
//         this.quantity = quantity;
//     }
    
//     public Double getPrice() {
//         return price;
//     }
    
//     public void setPrice(Double price) {
//         this.price = price;
//     }
    
//     public Double getSubtotal() {
//         return price * quantity;
//     }
// }
