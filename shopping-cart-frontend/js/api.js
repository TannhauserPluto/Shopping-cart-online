// API配置
const API_BASE_URL = 'http://localhost:8080/api';

// API请求封装
const api = {
    // 产品相关API
    products: {
        getAll: async () => {
            const response = await fetch(`${API_BASE_URL}/products`);
            if (!response.ok) throw new Error('获取产品列表失败');
            return response.json();
        },

        getById: async (id) => {
            const response = await fetch(`${API_BASE_URL}/products/${id}`);
            if (!response.ok) throw new Error('获取产品详情失败');
            return response.json();
        },

        search: async (keyword) => {
            const response = await fetch(`${API_BASE_URL}/products/search?keyword=${encodeURIComponent(keyword)}`);
            if (!response.ok) throw new Error('搜索产品失败');
            return response.json();
        },

        filterByPrice: async (minPrice, maxPrice) => {
            let url = `${API_BASE_URL}/products/filter/price?`;
            if (minPrice) url += `minPrice=${minPrice}&`;
            if (maxPrice) url += `maxPrice=${maxPrice}`;
            
            const response = await fetch(url);
            if (!response.ok) throw new Error('筛选产品失败');
            return response.json();
        },

        getInStock: async () => {
            const response = await fetch(`${API_BASE_URL}/products/in-stock`);
            if (!response.ok) throw new Error('获取有货产品失败');
            return response.json();
        }
    },

    // 购物车相关API（修正版）
    cart: {
        // ✅ 修正：使用 /cart/user/{userId}
        getByUserId: async (userId) => {
            const url = `${API_BASE_URL}/cart/user/${userId}`;
            console.log('🔍 请求购物车:', url);
            
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`获取购物车失败 (HTTP ${response.status})`);
            }
            return response.json();
        },

        // ✅ 修正：使用 /cart/add，传递完整的请求体
        addItem: async (userId, productId, quantity = 1) => {
            const url = `${API_BASE_URL}/cart/add`;
            const body = {
                userId: parseInt(userId),
                productId: parseInt(productId),
                quantity: parseInt(quantity)
            };
            
            console.log('🔍 添加到购物车:', url, body);
            
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json; charset=utf-8'
                },
                body: JSON.stringify(body)
            });
            
            if (!response.ok) {
                const errorText = await response.text();
                console.error('❌ 添加失败:', errorText);
                throw new Error(errorText || '添加到购物车失败');
            }
            
            return response.json();
        },

        // ✅ 修正：使用 /cart/update，传递完整的请求体
        updateQuantity: async (userId, productId, quantity) => {
            const url = `${API_BASE_URL}/cart/update`;
            const body = {
                userId: parseInt(userId),
                productId: parseInt(productId),
                quantity: parseInt(quantity)
            };
            
            console.log('🔍 更新数量:', url, body);
            
            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json; charset=utf-8'
                },
                body: JSON.stringify(body)
            });
            
            if (!response.ok) {
                const errorText = await response.text();
                console.error('❌ 更新失败:', errorText);
                throw new Error(errorText || '更新数量失败');
            }
            
            return response.json();
        },

        // ✅ 修正：使用 /cart/remove/{itemId}，参数名修正
        removeItem: async (itemId) => {
            const url = `${API_BASE_URL}/cart/remove/${itemId}`;
            console.log('🔍 删除购物车项:', url);
            
            const response = await fetch(url, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                throw new Error('移除商品失败');
            }
            
            return response.json();
        },

        // ✅ 修正：使用 /cart/clear/{userId}
        clear: async (userId) => {
            const url = `${API_BASE_URL}/cart/clear/${userId}`;
            console.log('🔍 清空购物车:', url);
            
            const response = await fetch(url, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                throw new Error('清空购物车失败');
            }
            
            return response.json();
        },

        // ✅ 新增：获取购物车摘要
        getSummary: async (userId) => {
            const url = `${API_BASE_URL}/cart/summary/${userId}`;
            console.log('🔍 获取购物车摘要:', url);
            
            const response = await fetch(url);
            
            if (!response.ok) {
                throw new Error('获取购物车摘要失败');
            }
            
            return response.json();
        }
    }
};

// ✅ 添加：测试函数（用于调试）
async function testAPI() {
    console.log('=== 开始测试 API ===');
    
    try {
        // 测试 1: 获取购物车
        console.log('\n📝 测试 1: 获取购物车');
        const cart = await api.cart.getByUserId(1);
        console.log('✅ 成功:', cart);
        
        // 测试 2: 添加商品
        console.log('\n📝 测试 2: 添加商品到购物车');
        const addResult = await api.cart.addItem(1, 1, 1);
        console.log('✅ 成功:', addResult);
        
        // 测试 3: 获取摘要
        console.log('\n📝 测试 3: 获取购物车摘要');
        const summary = await api.cart.getSummary(1);
        console.log('✅ 成功:', summary);
        
        console.log('\n✅ 所有测试通过！');
        
    } catch (error) {
        console.error('❌ 测试失败:', error);
    }
}

// 在控制台运行 testAPI() 来测试
console.log('💡 提示: 在控制台输入 testAPI() 来测试 API');
