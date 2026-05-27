const api = require('../../utils/api');

function getCart() {
  try { return wx.getStorageSync('cart') || []; } catch (e) { return []; }
}
function saveCart(cart) { wx.setStorageSync('cart', cart); }

Page({
  data: {
    categories: [],
    dishes: [],
    activeCategory: '',
    keyword: '',
    cartCount: 0
  },

  onShow() {
    this.loadCategories();
    this.loadDishes();
    this.updateCartCount();
  },

  updateCartCount() {
    const cart = getCart();
    const count = cart.reduce((s, i) => s + (i.quantity || 1), 0);
    this.setData({ cartCount: count });
  },

  async loadCategories() {
    try {
      const res = await api.get('/dishes/categories');
      if (res.code === 200 && res.data) {
        const cats = Array.isArray(res.data) ? res.data : res.data.content || res.data.records || [];
        this.setData({ categories: cats });
      }
    } catch (e) { console.warn('Failed to load categories', e); }
  },

  async loadDishes() {
    const { activeCategory, keyword } = this.data;
    let url = '/dishes?page=1&size=20&status=ON';
    if (activeCategory) url += '&categoryId=' + activeCategory;
    if (keyword) url += '&keyword=' + encodeURIComponent(keyword);
    try {
      const res = await api.get(url);
      if (res.code === 200 && res.data) {
        var dishes = Array.isArray(res.data) ? res.data : res.data.content || res.data.records || [];
        // 仅保留已上架且有库存的菜品
        dishes = dishes.filter(function(d) {
          return d.status === 'ON' && (d.dailyStock || d.stock || 0) > 0;
        });
        if (dishes.length > 0) {
          this.setData({ dishes: dishes });
          return;
        }
      }
      // fallback: 过滤 mock 数据
      var mock = getMockDishes().filter(function(d) { return d.stock > 0; });
      this.setData({ dishes: mock });
    } catch (e) {
      console.warn('Failed to load dishes', e);
      var mock = getMockDishes().filter(function(d) { return d.stock > 0; });
      this.setData({ dishes: mock });
    }
  },

  switchCategory(e) {
    const id = e.currentTarget.dataset.id;
    this.setData({ activeCategory: id }, () => this.loadDishes());
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value });
    clearTimeout(this._searchTimer);
    this._searchTimer = setTimeout(() => this.loadDishes(), 400);
  },

  addToCart(e) {
    var dish = e.currentTarget.dataset.item;
    var stock = dish.dailyStock !== undefined ? dish.dailyStock : (dish.stock || 999);
    if (stock <= 0) {
      wx.showToast({ title: '该菜品已售罄', icon: 'none' });
      return;
    }
    var cart = getCart();
    var existing = null;
    for (var i = 0; i < cart.length; i++) {
      if (cart[i].id === dish.id) { existing = cart[i]; break; }
    }
    if (existing) {
      if (existing.quantity >= stock) {
        wx.showToast({ title: '库存不足，最多' + stock + '份', icon: 'none' });
        return;
      }
      existing.quantity = (existing.quantity || 1) + 1;
    } else {
      cart.push({
        id: dish.id,
        name: dish.name,
        price: dish.price,
        image: dish.image || '',
        canteenId: dish.canteenId || null,
        canteenName: dish.canteenName || '',
        windowId: dish.windowId || null,
        windowName: dish.windowName || '',
        dailyStock: stock,
        quantity: 1
      });
    }
    saveCart(cart);
    this.updateCartCount();
    wx.showToast({ title: '已添加', icon: 'success', duration: 800 });
  },

  goReserve() {
    wx.navigateTo({ url: '/pages/reserve/reserve' });
  }
});

// 菜品 mock 数据（API 无数据时回退）
var MOCK_IMG = [
  'https://modao.cc/agent-py/media/generated_images/2026-04-30/0867b32ab91f46e6bebcc1e0774bd519.jpg',
  'https://modao.cc/agent-py/media/generated_images/2026-04-30/252859d8eeb54530ae1ac9edfcb941a3.jpg',
  'https://modao.cc/agent-py/media/generated_images/2026-04-30/8e50eb8d4716443684ccad60f9798a69.jpg',
  'https://modao.cc/agent-py/media/generated_images/2026-04-30/0f60c624817749e199bfc275a3a3329b.jpg',
  'https://modao.cc/agent-py/media/generated_images/2026-04-30/3821939504fd4fb98a412f322590733d.jpg',
  'https://modao.cc/agent-py/media/generated_images/2026-04-30/35b9a2fda2634e3791981bb293bd4fd4.jpg',
  'https://modao.cc/agent-py/media/generated_images/2026-04-30/bcbc220d37b74ff9bb04b08a84a7cf3d.jpg',
  'https://modao.cc/agent-py/media/generated_images/2026-04-30/9210c0fca13041b895c14aaac42a3d08.jpg',
  'https://modao.cc/agent-py/media/generated_images/2026-04-30/a62173a763ea4eba9c0861c91d34da2f.jpg'
];
function getMockDishes() {
  return [
    { id:1, name:'秘制红烧牛腩', canteenName:'第一食堂', price:12.00, calories:450, stock:85, tags:['高蛋白'], image:MOCK_IMG[0] },
    { id:2, name:'宫保鸡丁套饭', canteenName:'第一食堂', price:10.00, calories:380, stock:50, tags:['咸甜适中'], image:MOCK_IMG[1] },
    { id:3, name:'清蒸鲈鱼片', canteenName:'教工餐厅', price:15.00, calories:280, stock:30, tags:['健康轻食'], image:MOCK_IMG[2] },
    { id:4, name:'番茄炒蛋面', canteenName:'第二食堂', price:8.50, calories:380, stock:45, tags:['营养均衡'], image:MOCK_IMG[3] },
    { id:5, name:'糖醋排骨', canteenName:'清真食堂', price:14.00, calories:520, stock:20, tags:['招牌必点'], image:MOCK_IMG[4] },
    { id:6, name:'手撕包菜', canteenName:'第一食堂', price:4.50, calories:120, stock:0, tags:['清爽可口'], image:MOCK_IMG[5] },
    { id:7, name:'银耳雪梨汤', canteenName:'第一食堂', price:3.50, calories:80, stock:80, tags:['润肺养颜'], image:MOCK_IMG[6] },
    { id:8, name:'麻婆豆腐套饭', canteenName:'教工餐厅', price:8.50, calories:420, stock:35, tags:['麻辣鲜香'], image:MOCK_IMG[7] },
    { id:9, name:'鲜虾小馄饨', canteenName:'第二食堂', price:6.00, calories:200, stock:40, tags:['鲜美嫩滑'], image:MOCK_IMG[8] }
  ];
}
