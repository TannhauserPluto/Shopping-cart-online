// package com.nus.shoppingcart.controller;

// import com.nus.shoppingcart.dto.CartSummary;
// import com.nus.shoppingcart.model.CartItem;
// import com.nus.shoppingcart.service.CartService;
// import org.springframework.web.bind.annotation.*;
// import java.util.List;

// @RestController
// @RequestMapping("/api/cart")
// @CrossOrigin(origins = "http://localhost:3000")
// // @CrossOrigin(origins = "*")  // ✅ 添加跨域支持
// public class CartController {

//     private final CartService service;

//     public CartController(CartService service) {
//         this.service = service;
//     }

//     @GetMapping("/{userId}")
//     public List<CartItem> getCart(@PathVariable Long userId) {
//         return service.getCartByUser(userId);
//     }

//     // 新增：获取购物车摘要（包含总价）
//     @GetMapping("/{userId}/summary")
//     public CartSummary getCartSummary(@PathVariable Long userId) {
//         return service.getCartSummary(userId);
//     }

//     @PostMapping("/add")
//     public CartItem addToCart(@RequestParam Long userId, 
//                               @RequestParam Long productId, 
//                               @RequestParam int quantity) {
//         return service.addToCart(userId, productId, quantity);
//     }

//     @PutMapping("/update")
//     public CartItem updateQuantity(@RequestParam Long userId, 
//                                    @RequestParam Long productId, 
//                                    @RequestParam int quantity) {
//         return service.updateCartItemQuantity(userId, productId, quantity);
//     }

//     @DeleteMapping("/{itemId}")
//     public void removeFromCart(@PathVariable Long itemId) {
//         service.removeFromCart(itemId);
//     }

//     @DeleteMapping("/clear/{userId}")
//     public void clearCart(@PathVariable Long userId) {
//         service.clearCart(userId);
//     }
// }

package com.nus.shoppingcart.controller;

import com.nus.shoppingcart.dto.AddToCartRequest;
import com.nus.shoppingcart.dto.CartSummary;
import com.nus.shoppingcart.exception.BadRequestException;
import com.nus.shoppingcart.exception.ResourceNotFoundException;
import com.nus.shoppingcart.model.CartItem;
import com.nus.shoppingcart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    /**
     * 添加商品到购物车
     * POST /api/cart/add
     * Body: { "userId": 1, "productId": 1, "quantity": 2 }
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request) {
        try {
            System.out.println("收到添加购物车请求 - userId: " + request.getUserId() + 
                             ", productId: " + request.getProductId() + 
                             ", quantity: " + request.getQuantity());
            
            CartItem cartItem = cartService.addToCart(
                request.getUserId(), 
                request.getProductId(), 
                request.getQuantity()
            );
            
            return ResponseEntity.ok(cartItem);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("添加失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户购物车列表
     * GET /api/cart/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCartByUser(@PathVariable Long userId) {
        try {
            System.out.println("查询购物车 - userId: " + userId);
            List<CartItem> items = cartService.getCartByUser(userId);
            return ResponseEntity.ok(items);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("查询失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取购物车摘要（包含总价和总数量）
     * GET /api/cart/summary/{userId}
     */
    @GetMapping("/summary/{userId}")
    public ResponseEntity<?> getCartSummary(@PathVariable Long userId) {
        try {
            System.out.println("查询购物车摘要 - userId: " + userId);
            CartSummary summary = cartService.getCartSummary(userId);
            return ResponseEntity.ok(summary);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("查询失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新购物车商品数量
     * PUT /api/cart/update
     * Body: { "userId": 1, "productId": 1, "quantity": 5 }
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateCartItemQuantity(@RequestBody AddToCartRequest request) {
        try {
            System.out.println("更新购物车数量 - userId: " + request.getUserId() + 
                             ", productId: " + request.getProductId() + 
                             ", quantity: " + request.getQuantity());
            
            CartItem updated = cartService.updateCartItemQuantity(
                request.getUserId(), 
                request.getProductId(), 
                request.getQuantity()
            );
            
            if (updated == null) {
                // 数量为0时,商品被删除
                return ResponseEntity.ok(createSuccessResponse("商品已从购物车移除"));
            }
            
            return ResponseEntity.ok(updated);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("更新失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除购物车商品
     * DELETE /api/cart/remove/{itemId}
     */
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long itemId) {
        try {
            System.out.println("删除购物车商品 - itemId: " + itemId);
            cartService.removeFromCart(itemId);
            return ResponseEntity.ok(createSuccessResponse("删除成功"));
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("删除失败: " + e.getMessage()));
        }
    }
    
    /**
     * 清空购物车
     * DELETE /api/cart/clear/{userId}
     */
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        try {
            System.out.println("清空购物车 - userId: " + userId);
            cartService.clearCart(userId);
            return ResponseEntity.ok(createSuccessResponse("购物车已清空"));
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("清空失败: " + e.getMessage()));
        }
    }
    
    // 辅助方法：创建错误响应
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    // 辅助方法：创建成功响应
    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
