// 购物车管理模块
const CartManager = {
    cartItems: [],
    
    // 获取当前用户ID
    getCurrentUserId() {
        return document.getElementById('userSelect').value;
    },

    // 加载购物车
    async loadCart() {
        try {
            const userId = this.getCurrentUserId();
            console.log('🔍 加载购物车, userId:', userId);
            this.cartItems = await api.cart.getByUserId(userId);
            console.log('✅ 购物车数据:', this.cartItems);
            this.renderCart();
            this.updateCartCount();
        } catch (error) {
            console.error('❌ 加载购物车失败:', error);
            this.cartItems = [];
            this.renderCart();
        }
    },

    // 渲染购物车
    renderCart() {
        const cartItemsContainer = document.getElementById('cartItems');
        const cartTotal = document.getElementById('cartTotal');

        if (this.cartItems.length === 0) {
            cartItemsContainer.innerHTML = `
                <div class="cart-empty">
                    <i class="fas fa-shopping-cart"></i>
                    <p>购物车是空的</p>
                </div>
            `;
            cartTotal.textContent = '¥0.00';
            return;
        }

        let total = 0;
        cartItemsContainer.innerHTML = this.cartItems.map(item => {
            const subtotal = item.product.price * item.quantity;
            total += subtotal;
            
            return `
                <div class="cart-item" data-item-id="${item.id}" data-product-id="${item.product.id}">
                    <img src="${item.product.imageUrl || 'images/placeholder.jpg'}" 
                         alt="${item.product.name}" 
                         class="cart-item-image"
                         onerror="this.src='images/placeholder.jpg'">
                    <div class="cart-item-info">
                        <div class="cart-item-name">${item.product.name}</div>
                        <div class="cart-item-price">¥${item.product.price.toFixed(2)}</div>
                        <div class="quantity-controls">
                            <!-- ✅ 修正：传递 itemId, productId, 当前数量, 变化量 -->
                            <button onclick="CartManager.updateQuantity(${item.id}, ${item.product.id}, ${item.quantity}, -1)"
                                    title="${item.quantity === 1 ? '删除商品' : '减少数量'}">
                                <i class="fas ${item.quantity === 1 ? 'fa-trash' : 'fa-minus'}"></i>
                            </button>
                            <span>${item.quantity}</span>
                            <button onclick="CartManager.updateQuantity(${item.id}, ${item.product.id}, ${item.quantity}, 1)"
                                    ${item.quantity >= item.product.stock ? 'disabled' : ''}
                                    title="增加数量">
                                <i class="fas fa-plus"></i>
                            </button>
                        </div>
                        ${item.product.stock < 10 ? 
                            `<div class="stock-warning">仅剩 ${item.product.stock} 件</div>` : 
                            ''}
                    </div>
                    <!-- ✅ 修正：传递 itemId -->
                    <div class="cart-item-remove" onclick="CartManager.removeFromCart(${item.id})"
                         title="删除商品">
                        <i class="fas fa-trash"></i>
                    </div>
                </div>
            `;
        }).join('');

        cartTotal.textContent = `¥${total.toFixed(2)}`;
    },

    // 更新购物车数量显示
    updateCartCount() {
        const cartCount = document.getElementById('cartCount');
        const totalCount = this.cartItems.reduce((sum, item) => sum + item.quantity, 0);
        cartCount.textContent = totalCount;
        
        // 如果有徽章，也更新徽章
        const badge = document.querySelector('.cart-badge');
        if (badge) {
            badge.textContent = totalCount;
            badge.style.display = totalCount > 0 ? 'block' : 'none';
        }
    },

    // 添加到购物车
    async addToCart(productId, quantity = 1) {
        try {
            const userId = this.getCurrentUserId();
            console.log('🛒 添加到购物车:', { userId, productId, quantity });
            
            await api.cart.addItem(userId, productId, quantity);
            await this.loadCart();
            showToast('已添加到购物车', 'success');
            
            // 如果购物车是打开的，保持打开状态
            if (document.getElementById('cartSidebar').classList.contains('active')) {
                this.showCart();
            }
        } catch (error) {
            console.error('❌ 添加到购物车失败:', error);
            showToast(error.message || '添加失败，请稍后重试', 'error');
        }
    },

    // ✅ 修正：更新商品数量
    async updateQuantity(itemId, productId, currentQuantity, change) {
        const newQuantity = currentQuantity + change;
        
        console.log('🔍 更新数量:', {
            itemId,
            productId,
            currentQuantity,
            change,
            newQuantity
        });
        
        // ✅ 关键：如果新数量小于1，则删除商品
        if (newQuantity < 1) {
            console.log('⚠️ 数量将变为0，准备删除商品');
            await this.removeFromCart(itemId);
            return;
        }

        try {
            const userId = this.getCurrentUserId();
            await api.cart.updateQuantity(userId, productId, newQuantity);
            await this.loadCart();
            console.log('✅ 更新成功');
        } catch (error) {
            console.error('❌ 更新数量失败:', error);
            showToast(error.message || '更新失败，请稍后重试', 'error');
        }
    },

    // ✅ 修正：从购物车移除（使用 itemId）
    async removeFromCart(itemId) {
        console.log('🗑️ 准备删除购物车项, itemId:', itemId);
        
        if (!confirm('确定要移除这个商品吗？')) {
            console.log('❌ 用户取消删除');
            return;
        }

        try {
            // ✅ 关键：使用 itemId 调用删除 API
            await api.cart.removeItem(itemId);
            await this.loadCart();
            showToast('已从购物车移除', 'info');
            console.log('✅ 删除成功');
        } catch (error) {
            console.error('❌ 移除失败:', error);
            showToast('移除失败，请稍后重试', 'error');
        }
    },

    // 清空购物车
    async clearCart() {
        if (!confirm('确定要清空购物车吗？')) return;

        try {
            const userId = this.getCurrentUserId();
            console.log('🗑️ 清空购物车, userId:', userId);
            
            await api.cart.clear(userId);
            await this.loadCart();
            showToast('购物车已清空', 'info');
            console.log('✅ 清空成功');
        } catch (error) {
            console.error('❌ 清空购物车失败:', error);
            showToast('清空失败，请稍后重试', 'error');
        }
    },

    // 显示购物车
    showCart() {
        document.getElementById('cartSidebar').classList.add('active');
        // 显示时刷新购物车
        this.loadCart();
    },

    // 隐藏购物车
    hideCart() {
        document.getElementById('cartSidebar').classList.remove('active');
    },

    // 结算
    async checkout() {
        if (this.cartItems.length === 0) {
            showToast('购物车是空的', 'error');
            return;
        }

        const total = this.cartItems.reduce((sum, item) => 
            sum + (item.product.price * item.quantity), 0
        );

        if (confirm(`总计：¥${total.toFixed(2)}\n确定要结算吗？`)) {
            try {
                console.log('💳 开始结算...');
                // 这里可以添加实际的结算逻辑
                await this.clearCart();
                showToast('结算成功！感谢您的购买', 'success');
                this.hideCart();
                console.log('✅ 结算完成');
            } catch (error) {
                console.error('❌ 结算失败:', error);
                showToast('结算失败，请稍后重试', 'error');
            }
        }
    }
};

// Toast 提示函数（如果还没有的话）
function showToast(message, type = 'info') {
    // 移除已存在的 toast
    const existingToast = document.querySelector('.toast');
    if (existingToast) {
        existingToast.remove();
    }
    
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    
    const icon = {
        'success': '✅',
        'error': '❌',
        'info': 'ℹ️',
        'warning': '⚠️'
    }[type] || 'ℹ️';
    
    toast.innerHTML = `
        <span class="toast-icon">${icon}</span>
        <span class="toast-message">${message}</span>
    `;
    
    document.body.appendChild(toast);
    
    // 触发动画
    setTimeout(() => {
        toast.classList.add('show');
    }, 100);
    
    // 自动移除
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}
