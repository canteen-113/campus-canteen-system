const api = require('../../utils/api');

Page({
  data: {
    userName: '',
    userId: null,
    balance: '--',
    recommendList: [],
    announcements: [],
    pendingOrder: null
  },

  onShow() {
    const user = api.getUserInfo();
    if (user) {
      this.setData({ userName: user.name || '', userId: user.userId || user.id });
      this.loadData();
    }
  },

  async loadData() {
    const { userId } = this.data;
    if (!userId) return;

    this.loadBalance(userId);
    this.loadRecommend();
    this.loadAnnouncements();
    this.loadPendingOrder(userId);
  },

  async loadBalance(userId) {
    try {
      const res = await api.get('/users/' + userId);
      if (res.code === 200 && res.data) {
        this.setData({ balance: Number(res.data.balance || 0).toFixed(2) });
      }
    } catch (e) { console.warn('Failed to load balance', e); }
  },

  async loadRecommend() {
    try {
      const res = await api.get('/dishes/recommended');
      if (res.code === 200 && res.data) {
        const list = (Array.isArray(res.data) ? res.data : res.data.content || res.data.records || []).slice(0, 3);
        this.setData({ recommendList: list });
      }
    } catch (e) { console.warn('Failed to load recommend', e); }
  },

  async loadAnnouncements() {
    try {
      const res = await api.get('/announcements');
      if (res.code === 200 && res.data) {
        const anns = Array.isArray(res.data) ? res.data : res.data.content || res.data.records || [];
        this.setData({ announcements: anns.slice(0, 2) });
      }
    } catch (e) { console.warn('Failed to load announcements', e); }
  },

  async loadPendingOrder(userId) {
    try {
      const res = await api.get('/orders?userId=' + userId + '&status=待取餐&size=1');
      if (res.code === 200 && res.data) {
        const orders = Array.isArray(res.data) ? res.data : res.data.content || res.data.records || [];
        if (orders.length > 0) {
          this.setData({ pendingOrder: orders[0] });
        }
      }
    } catch (e) { console.warn('Failed to load pending orders', e); }
  },

  quickRecharge() {
    wx.showModal({
      title: '快捷充值',
      content: '请输入充值金额',
      editable: true,
      success: async (res) => {
        if (res.confirm && res.content) {
          const amount = parseFloat(res.content);
          if (isNaN(amount) || amount <= 0) return;
          try {
            const resp = await api.post('/users/' + this.data.userId + '/recharge?amount=' + amount);
            if (resp.code === 200) {
              wx.showToast({ title: '充值成功', icon: 'success' });
              this.loadBalance(this.data.userId);
            } else {
              wx.showToast({ title: resp.message || '充值失败', icon: 'none' });
            }
          } catch (e) { wx.showToast({ title: '充值失败', icon: 'none' }); }
        }
      }
    });
  }
});
