package com.nus.shoppingcart.exception;

public class InsufficientStockException extends RuntimeException {
    private String productName;
    private int requestedQuantity;
    private int availableStock;

    public InsufficientStockException(String productName, int requestedQuantity, int availableStock) {
        super(String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d", 
            productName, requestedQuantity, availableStock));
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }

    public String getProductName() {
        return productName;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableStock() {
        return availableStock;
    }
}
