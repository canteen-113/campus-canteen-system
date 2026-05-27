const api = require('../../utils/api');

Page({
  data: {
    balance: '--',
    currentMonth: '',
    groupedTransactions: []
  },

  onShow() {
    const now = new Date();
    this.setData({ currentMonth: now.getFullYear() + '年' + (now.getMonth() + 1) + '月' });
    this.loadData();
  },

  async loadData() {
    const user = api.getUserInfo();
    if (!user) return;
    const userId = user.userId || user.id;

    try {
      const userRes = await api.get('/users/' + userId);
      if (userRes.code === 200 && userRes.data) {
        this.setData({ balance: Number(userRes.data.balance || 0).toFixed(2) });
      }
    } catch (e) { console.warn('Failed to load balance', e); }

    try {
      const txRes = await api.get('/users/' + userId + '/transactions');
      if (txRes.code === 200 && txRes.data) {
        const txs = Array.isArray(txRes.data) ? txRes.data : txRes.data.content || txRes.data.records || [];
        const grouped = this.groupByDate(txs);
        this.setData({ groupedTransactions: grouped });
      }
    } catch (e) { console.warn('Failed to load transactions', e); }
  },

  groupByDate(txs) {
    const groups = {};
    txs.forEach(tx => {
      const date = (tx.createTime || tx.tradeTime || '').slice(0, 10) || '未知日期';
      if (!groups[date]) groups[date] = [];
      const type = tx.type || tx.transactionType || '消费';
      const amount = tx.amount || 0;
      const isIncome = type === '充值' || type === '退款' || amount > 0;
      groups[date].push({
        id: tx.id,
        description: tx.description || tx.remark || type,
        time: (tx.createTime || tx.tradeTime || '').slice(11, 19) || '',
        source: tx.windowName || tx.source || '',
        amount: amount,
        absAmount: Math.abs(amount).toFixed(2),
        isIncome: isIncome
      });
    });
    return Object.keys(groups).map(date => ({ date, list: groups[date] }));
  },

  async recharge() {
    const user = api.getUserInfo();
    if (!user) return;
    const userId = user.userId || user.id;
    wx.showModal({
      title: '充值余额',
      content: '请输入充值金额',
      editable: true,
      success: async (res) => {
        if (res.confirm && res.content) {
          const amount = parseFloat(res.content);
          if (isNaN(amount) || amount <= 0) return;
          try {
            const resp = await api.post('/users/' + userId + '/recharge?amount=' + amount);
            if (resp.code === 200) {
              wx.showToast({ title: '充值成功', icon: 'success' });
              this.loadData();
            } else {
              wx.showToast({ title: resp.message || '充值失败', icon: 'none' });
            }
          } catch (e) { wx.showToast({ title: '充值失败', icon: 'none' }); }
        }
      }
    });
  }
});
