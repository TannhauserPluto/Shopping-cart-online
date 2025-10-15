package com.nus.shoppingcart.service;

import com.nus.shoppingcart.exception.BadRequestException;
import com.nus.shoppingcart.exception.ResourceNotFoundException;
import com.nus.shoppingcart.model.Product;
import com.nus.shoppingcart.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product getProductById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Transactional
    public Product createProduct(Product product) {
        validateProduct(product);
        return repository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (productDetails.getName() != null && !productDetails.getName().trim().isEmpty()) {
            product.setName(productDetails.getName());
        }
        if (productDetails.getDescription() != null) {
            product.setDescription(productDetails.getDescription());
        }
        if (productDetails.getPrice() != null) {
            if (productDetails.getPrice() < 0) {
                throw new BadRequestException("Product price must be non-negative");
            }
            product.setPrice(productDetails.getPrice());
        }
        if (productDetails.getStock() != null) {
            if (productDetails.getStock() < 0) {
                throw new BadRequestException("Product stock must be non-negative");
            }
            product.setStock(productDetails.getStock());
        }
        if (productDetails.getImageUrl() != null) {
            validateImageUrl(productDetails.getImageUrl());
            product.setImageUrl(productDetails.getImageUrl());
        }

        return repository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        repository.deleteById(id);
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new BadRequestException("Product name is required");
        }
        if (product.getPrice() == null || product.getPrice() < 0) {
            throw new BadRequestException("Product price must be non-negative");
        }
        if (product.getStock() == null || product.getStock() < 0) {
            throw new BadRequestException("Product stock must be non-negative");
        }
        if (product.getImageUrl() != null) {
            validateImageUrl(product.getImageUrl());
        }
    }

    private void validateImageUrl(String imageUrl) {
        if (!imageUrl.trim().isEmpty() && !isValidUrl(imageUrl)) {
            throw new BadRequestException("Invalid image URL format");
        }
    }

    private boolean isValidUrl(String url) {
        try {
            new java.net.URL(url);
            return true;
        } catch (java.net.MalformedURLException e) {
            return false;
        }
    }
}
