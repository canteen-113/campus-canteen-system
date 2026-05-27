const api = require('../../utils/api');

Page({
  data: {
    pickupCode: '----',
    pickupWindow: '--',
    pickupTime: '--'
  },

  onLoad(options) {
    if (options.code) {
      this.setData({ pickupCode: options.code });
    }
    this.loadOrder();
  },

  onShow() {
    if (!this.data.pickupCode || this.data.pickupCode === '----') {
      this.loadOrder();
    }
  },

  async loadOrder() {
    const user = api.getUserInfo();
    if (!user) return;
    const userId = user.userId || user.id;
    try {
      const res = await api.get('/orders?userId=' + userId + '&status=待取餐&size=1');
      if (res.code === 200 && res.data) {
        const orders = Array.isArray(res.data) ? res.data : res.data.content || res.data.records || [];
        if (orders.length > 0) {
          const order = orders[0];
          this.setData({
            pickupCode: '#' + (order.pickupCode || order.id || '----'),
            pickupWindow: (order.canteenName || '') + ' · ' + (order.windowName || '') + '窗口',
            pickupTime: order.mealPeriod || '--'
          });
        }
      }
    } catch (e) { console.warn('Failed to load pickup order', e); }
  }
});
