const api = require('../../utils/api');

function getCart() {
  try { return wx.getStorageSync('cart') || []; } catch (e) { return []; }
}
function saveCart(cart) {
  wx.setStorageSync('cart', cart);
}

Page({
  data: {
    cart: [],
    periods: [
      { period: 'BREAKFAST', label: '早餐' },
      { period: 'LUNCH', label: '午餐' },
      { period: 'DINNER', label: '晚餐' }
    ],
    selectedPeriod: 'LUNCH',
    reserveDate: '',
    subtotal: '0.00',
    discount: '0.00',
    total: '0.00',
    cardBalance: '--'
  },

  onShow() {
    this.recalc();
    this.loadBalance();
  },

  recalc() {
    var cart = getCart();
    var subtotal = 0;
    for (var i = 0; i < cart.length; i++) {
      subtotal += (cart[i].price || 0) * (cart[i].quantity || 1);
    }
    var deposit = 2.00;
    var discount = subtotal >= 10 ? 1.50 : 0;
    var total = subtotal + deposit - discount;

    var now = new Date();
    var dateStr = now.getFullYear() + '-' +
      String(now.getMonth() + 1).padStart(2, '0') + '-' +
      String(now.getDate()).padStart(2, '0');

    this.setData({
      cart: cart,
      subtotal: subtotal.toFixed(2),
      discount: discount.toFixed(2),
      total: total.toFixed(2),
      reserveDate: dateStr
    });
  },

  // 删除菜品
  onRemove(e) {
    var id = e.currentTarget.dataset.id;
    var cart = getCart();
    var newCart = [];
    for (var i = 0; i < cart.length; i++) {
      if (cart[i].id !== id) newCart.push(cart[i]);
    }
    saveCart(newCart);
    wx.showToast({ title: '已移除', icon: 'success', duration: 800 });
    this.recalc();
  },

  // 数量+1
  onPlus(e) {
    var id = e.currentTarget.dataset.id;
    var cart = getCart();
    for (var i = 0; i < cart.length; i++) {
      if (cart[i].id === id) {
        cart[i].quantity = (cart[i].quantity || 1) + 1;
        break;
      }
    }
    saveCart(cart);
    this.recalc();
  },

  // 数量-1（减到0则删除）
  onMinus(e) {
    var id = e.currentTarget.dataset.id;
    var cart = getCart();
    var removed = false;
    for (var i = 0; i < cart.length; i++) {
      if (cart[i].id === id) {
        var q = (cart[i].quantity || 1) - 1;
        if (q <= 0) {
          cart.splice(i, 1);
          removed = true;
        } else {
          cart[i].quantity = q;
        }
        break;
      }
    }
    saveCart(cart);
    wx.showToast({ title: removed ? '已移除' : '数量-1', icon: 'success', duration: 600 });
    this.recalc();
  },

  async loadBalance() {
    var user = api.getUserInfo();
    if (!user) return;
    var userId = user.userId || user.id;
    try {
      var res = await api.get('/users/' + userId);
      if (res.code === 200 && res.data) {
        this.setData({ cardBalance: Number(res.data.balance || 0).toFixed(2) });
      }
    } catch (e) { console.warn('Failed to load balance', e); }
  },

  selectPeriod(e) {
    this.setData({ selectedPeriod: e.currentTarget.dataset.period });
  },

  submitOrder: function() {
    var user = api.getUserInfo();
    if (!user) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    var userId = user.userId || user.id;
    var cart = getCart();
    if (cart.length === 0) {
      wx.showToast({ title: '购物车为空', icon: 'none' });
      return;
    }

    var items = cart.map(function(item) {
      return {
        dishId: item.id,
        dishName: item.name,
        price: item.price,
        quantity: item.quantity || 1
      };
    });

    var subtotal = 0;
    for (var i = 0; i < cart.length; i++) {
      subtotal += (cart[i].price || 0) * (cart[i].quantity || 1);
    }
    var deposit = 2.00;
    var discount = subtotal >= 10 ? 1.50 : 0;

    var that = this;
    wx.showModal({
      title: '确认下单',
      content: '实付 ¥' + (subtotal + deposit - discount).toFixed(2) + '，将从一卡通扣款',
      success: function(modalRes) {
        if (!modalRes.confirm) return;

        api.post('/orders', {
          userId: userId,
          canteenId: cart[0].canteenId,
          windowId: cart[0].windowId,
          items: items,
          mealPeriod: that.data.selectedPeriod,
          reserveDate: that.data.reserveDate,
          deposit: deposit,
          discount: discount,
          paymentMethod: 'CARD'
        }).then(function(res) {
          if (res.code === 200 && res.data) {
            wx.removeStorageSync('cart');
            wx.showToast({ title: '下单成功！', icon: 'success' });
            setTimeout(function() {
              wx.switchTab({ url: '/pages/orders/orders' });
            }, 1200);
          } else {
            wx.showToast({ title: res.message || '下单失败', icon: 'none' });
          }
        }).catch(function(e) {
          // 检查错误信息
          var msg = '下单失败';
          if (e && e.message) msg = e.message;
          wx.showToast({ title: msg, icon: 'none' });
        });
      }
    });
  }
});
