package com.nus.shoppingcart.controller;

import com.nus.shoppingcart.dto.ProductDTO;
import com.nus.shoppingcart.model.Product;
import com.nus.shoppingcart.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // 获取所有产品
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = service.getAllProducts();
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    // 根据ID获取产品
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Product product = service.getProductById(id);
        return ResponseEntity.ok(convertToDTO(product));
    }

    // 创建新产品
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        Product savedProduct = service.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedProduct));
    }

    // 更新产品
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id, 
            @RequestBody ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        Product updatedProduct = service.updateProduct(id, product);
        return ResponseEntity.ok(convertToDTO(updatedProduct));
    }

    // 删除产品
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // 搜索产品（根据名称）
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String keyword) {
        List<Product> products = service.getAllProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                           (p.getDescription() != null && 
                            p.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .collect(Collectors.toList());
        
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(productDTOs);
    }

    // 根据价格范围筛选产品
    @GetMapping("/filter/price")
    public ResponseEntity<List<ProductDTO>> filterByPrice(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        
        List<Product> products = service.getAllProducts().stream()
                .filter(p -> {
                    if (minPrice != null && p.getPrice() < minPrice) return false;
                    if (maxPrice != null && p.getPrice() > maxPrice) return false;
                    return true;
                })
                .collect(Collectors.toList());
        
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(productDTOs);
    }

    // 获取有库存的产品
    @GetMapping("/in-stock")
    public ResponseEntity<List<ProductDTO>> getInStockProducts() {
        List<Product> products = service.getAllProducts().stream()
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());
        
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(productDTOs);
    }

    // 转换方法：Product -> ProductDTO
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());
        return dto;
    }

    // 转换方法：ProductDTO -> Product
    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
        return product;
    }
}
