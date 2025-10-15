USE shopping_cart;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 清空现有数据
DELETE FROM cart_item WHERE 1=1;
DELETE FROM product WHERE 1=1;
DELETE FROM user WHERE 1=1;

-- 重置自增ID
ALTER TABLE user AUTO_INCREMENT = 1;
ALTER TABLE product AUTO_INCREMENT = 1;
ALTER TABLE cart_item AUTO_INCREMENT = 1;

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 插入用户数据
INSERT INTO user (username, password, email) VALUES
('admin', 'admin123', 'admin@shop.com'),
('alice', 'password123', 'alice@example.com'),
('bob', 'password123', 'bob@example.com'),
('charlie', 'password123', 'charlie@example.com');

-- 插入产品数据（使用实际的字段名）
INSERT INTO product (name, description, price, stock, image_url) VALUES 
('Laptop', 'High-performance laptop with 16GB RAM', 999.99, 50, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500'),
('Smartphone', 'Latest model smartphone with 5G', 699.99, 100, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500'),
('Headphones', 'Wireless noise-cancelling headphones', 199.99, 75, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500'),
('Keyboard', 'Mechanical gaming keyboard', 89.99, 120, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500'),
('Mouse', 'Ergonomic wireless mouse', 49.99, 200, 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=500'),
('Monitor', '27-inch 4K display monitor', 399.99, 30, 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500'),
('Webcam', 'HD webcam for video calls', 79.99, 85, 'https://images.unsplash.com/photo-1587826080692-f439cd0b70da?w=500'),
('USB Hub', '7-port USB 3.0 hub', 29.99, 150, 'https://images.unsplash.com/photo-1625948515291-69613efd103f?w=500'),
('External SSD', '1TB portable SSD', 149.99, 60, 'https://images.unsplash.com/photo-1628557044797-f21a177c37ec?w=500'),
('Tablet', '10-inch tablet with stylus', 449.99, 40, 'https://images.unsplash.com/photo-1561154464-82e9adf32764?w=500');

-- 插入购物车数据
INSERT INTO cart_item (user_id, product_id, quantity) VALUES 
(1, 1, 1),
(1, 3, 2),
(2, 2, 1),
(2, 5, 3);

-- 验证数据
SELECT '=== Users ===' as info;
SELECT * FROM user;

SELECT '=== Products ===' as info;
SELECT id, name, price, stock, image_url FROM product;

SELECT '=== Cart Items ===' as info;
SELECT ci.id, u.username, p.name as product_name, ci.quantity, p.price
FROM cart_item ci
JOIN user u ON ci.user_id = u.id
JOIN product p ON ci.product_id = p.id;
