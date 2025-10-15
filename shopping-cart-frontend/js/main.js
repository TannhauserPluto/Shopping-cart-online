// 主程序入口
document.addEventListener('DOMContentLoaded', () => {
    // 初始化
    init();
    
    // 绑定事件
    bindEvents();
});

// 初始化函数
async function init() {
    // 加载产品
    await ProductManager.loadProducts();
    
    // 加载购物车
    await CartManager.loadCart();
}

// 绑定全局事件
function bindEvents() {
    // 搜索功能
    const searchInput = document.getElementById('searchInput');
    const searchBtn = document.getElementById('searchBtn');
    
    searchBtn.addEventListener('click', () => {
        ProductManager.searchProducts(searchInput.value);
    });
    
    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            ProductManager.searchProducts(searchInput.value);
        }
    });

    // 价格筛选
    const filterBtn = document.getElementById('filterBtn');
    const resetFilterBtn = document.getElementById('resetFilterBtn');
    const minPriceInput = document.getElementById('minPrice');
    const maxPriceInput = document.getElementById('maxPrice');
    
    filterBtn.addEventListener('click', () => {
        const minPrice = minPriceInput.value ? parseFloat(minPriceInput.value) : null;
        const maxPrice = maxPriceInput.value ? parseFloat(maxPriceInput.value) : null;
        ProductManager.filterByPrice(minPrice, maxPrice);
    });
    
    resetFilterBtn.addEventListener('click', () => {
        minPriceInput.value = '';
        maxPriceInput.value = '';
        document.getElementById('inStockOnly').checked = false;
        ProductManager.loadProducts();
    });

    // 仅显示有货商品
    const inStockOnly = document.getElementById('inStockOnly');
    inStockOnly.addEventListener('change', (e) => {
        if (e.target.checked) {
            ProductManager.showInStockOnly();
        } else {
            ProductManager.loadProducts();
        }
    });

    // 购物车相关
    const cartIcon = document.getElementById('cartIcon');
    const closeCart = document.getElementById('closeCart');
    const checkoutBtn = document.getElementById('checkoutBtn');
    const clearCartBtn = document.getElementById('clearCartBtn');
    
    cartIcon.addEventListener('click', () => {
        CartManager.showCart();
    });
    
    closeCart.addEventListener('click', () => {
        CartManager.hideCart();
    });
    
    checkoutBtn.addEventListener('click', () => {
        CartManager.checkout();
    });
    
    clearCartBtn.addEventListener('click', () => {
        CartManager.clearCart();
    });

    // 用户切换
    const userSelect = document.getElementById('userSelect');
    userSelect.addEventListener('change', async () => {
        await CartManager.loadCart();
        showToast(`已切换到用户: ${userSelect.options[userSelect.selectedIndex].text}`, 'info');
    });

    // 模态框关闭
    const closeModal = document.getElementById('closeModal');
    const productModal = document.getElementById('productModal');
    
    closeModal.addEventListener('click', () => {
        productModal.style.display = 'none';
    });
    
    window.addEventListener('click', (e) => {
        if (e.target === productModal) {
            productModal.style.display = 'none';
        }
    });
}

// 显示加载状态
function showLoading() {
    const productsGrid = document.getElementById('productsGrid');
    productsGrid.innerHTML = '<div class="loading"></div>';
}

// 隐藏加载状态
function hideLoading() {
    // 加载完成后会被产品列表替换
}

// 显示提示消息
function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// 防抖函数
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// 为搜索添加防抖
const debouncedSearch = debounce((keyword) => {
    ProductManager.searchProducts(keyword);
}, 500);

// 优化搜索输入
document.getElementById('searchInput').addEventListener('input', (e) => {
    debouncedSearch(e.target.value);
});
