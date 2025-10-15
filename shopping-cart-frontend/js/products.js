// 产品管理模块
const ProductManager = {
    currentProducts: [],

    // 加载所有产品
    async loadProducts() {
        try {
            showLoading();
            this.currentProducts = await api.products.getAll();
            this.renderProducts(this.currentProducts);
        } catch (error) {
            console.error('加载产品失败:', error);
            showToast('加载产品失败，请稍后重试', 'error');
        } finally {
            hideLoading();
        }
    },

    // 渲染产品列表
    renderProducts(products) {
        const productsGrid = document.getElementById('productsGrid');
        
        if (products.length === 0) {
            productsGrid.innerHTML = `
                <div class="no-products">
                    <i class="fas fa-box-open"></i>
                    <p>暂无商品</p>
                </div>
            `;
            return;
        }

        productsGrid.innerHTML = products.map(product => `
            <div class="product-card" data-product-id="${product.id}">
                <img src="${product.imageUrl || 'images/placeholder.jpg'}" 
                     alt="${product.name}" 
                     class="product-image"
                     onerror="this.src='images/placeholder.jpg'">
                <div class="product-info">
                    <h3 class="product-name">${product.name}</h3>
                    <p class="product-description">${product.description || '暂无描述'}</p>
                    <div class="product-price">¥${product.price.toFixed(2)}</div>
                    <div class="product-stock ${this.getStockClass(product.stock)}">
                        ${this.getStockText(product.stock)}
                    </div>
                    <div class="product-actions">
                        <button class="btn btn-primary add-to-cart" 
                                data-product-id="${product.id}"
                                ${product.stock === 0 ? 'disabled' : ''}>
                            <i class="fas fa-cart-plus"></i> 加入购物车
                        </button>
                        <button class="btn btn-secondary view-details" 
                                data-product-id="${product.id}">
                            <i class="fas fa-eye"></i> 查看详情
                        </button>
                    </div>
                </div>
            </div>
        `).join('');

        // 绑定事件
        this.bindProductEvents();
    },

    // 获取库存样式类
    getStockClass(stock) {
        if (stock === 0) return 'stock-out';
        if (stock < 10) return 'stock-low';
        return '';
    },

    // 获取库存文本
    getStockText(stock) {
        if (stock === 0) return '已售罄';
        if (stock < 10) return `仅剩 ${stock} 件`;
        return `库存: ${stock} 件`;
    },

    // 绑定产品事件
    bindProductEvents() {
        // 添加到购物车
        document.querySelectorAll('.add-to-cart').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                e.stopPropagation();
                const productId = btn.dataset.productId;
                await CartManager.addToCart(productId);
            });
        });

        // 查看详情
        document.querySelectorAll('.view-details').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                e.stopPropagation();
                const productId = btn.dataset.productId;
                await this.showProductDetails(productId);
            });
        });

        // 点击卡片也显示详情
        document.querySelectorAll('.product-card').forEach(card => {
            card.addEventListener('click', async (e) => {
                if (e.target.closest('.product-actions')) return;
                const productId = card.dataset.productId;
                await this.showProductDetails(productId);
            });
        });
    },

    // 显示产品详情
    async showProductDetails(productId) {
        try {
            const product = await api.products.getById(productId);
            const modal = document.getElementById('productModal');
            const modalBody = document.getElementById('modalBody');

            modalBody.innerHTML = `
                <div class="modal-product">
                    <div class="modal-product-image">
                        <img src="${product.imageUrl || 'images/placeholder.jpg'}" 
                             alt="${product.name}"
                             onerror="this.src='images/placeholder.jpg'">
                    </div>
                    <div class="modal-product-info">
                        <h2>${product.name}</h2>
                        <div class="product-price">¥${product.price.toFixed(2)}</div>
                        <div class="product-stock ${this.getStockClass(product.stock)}">
                            ${this.getStockText(product.stock)}
                        </div>
                        <p class="product-description">${product.description || '暂无详细描述'}</p>
                        <button class="btn btn-primary" 
                                onclick="CartManager.addToCart(${product.id})"
                                ${product.stock === 0 ? 'disabled' : ''}>
                            <i class="fas fa-cart-plus"></i> 加入购物车
                        </button>
                    </div>
                </div>
            `;

            modal.style.display = 'block';
        } catch (error) {
            console.error('获取产品详情失败:', error);
            showToast('获取产品详情失败', 'error');
        }
    },

    // 搜索产品
    async searchProducts(keyword) {
        if (!keyword.trim()) {
            await this.loadProducts();
            return;
        }

        try {
            showLoading();
            const products = await api.products.search(keyword);
            this.renderProducts(products);
        } catch (error) {
            console.error('搜索失败:', error);
            showToast('搜索失败，请稍后重试', 'error');
        } finally {
            hideLoading();
        }
    },

    // 按价格筛选
    async filterByPrice(minPrice, maxPrice) {
        try {
            showLoading();
            const products = await api.products.filterByPrice(minPrice, maxPrice);
            this.renderProducts(products);
        } catch (error) {
            console.error('筛选失败:', error);
            showToast('筛选失败，请稍后重试', 'error');
        } finally {
            hideLoading();
        }
    },

    // 显示有货产品
    async showInStockOnly() {
        try {
            showLoading();
            const products = await api.products.getInStock();
            this.renderProducts(products);
        } catch (error) {
            console.error('筛选失败:', error);
            showToast('筛选失败，请稍后重试', 'error');
        } finally {
            hideLoading();
        }
    }
};
