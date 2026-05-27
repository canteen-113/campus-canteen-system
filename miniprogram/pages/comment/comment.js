const api = require('../../utils/api');

Page({
  data: {
    orderId: null,
    dishImage: '',
    orderDate: '',
    selectedRating: 4,
    ratingHints: ['很差', '一般', '不错', '非常好，下次还会点！', '完美！强烈推荐'],
    tags: ['咸淡适中', '分量足', '食材新鲜', '出餐快', '包装精美'],
    selectedTags: { '咸淡适中': true },
    commentText: '',
    isAnonymous: false
  },

  onLoad(options) {
    this.setData({ orderId: options.orderId || null });
    const now = new Date();
    this.setData({ orderDate: now.getFullYear() + '-' + String(now.getMonth()+1).padStart(2,'0') + '-' + String(now.getDate()).padStart(2,'0') });
  },

  setRating(e) {
    this.setData({ selectedRating: parseInt(e.currentTarget.dataset.value) });
  },

  toggleTag(e) {
    const tag = e.currentTarget.dataset.tag;
    const selectedTags = { ...this.data.selectedTags };
    if (selectedTags[tag]) {
      delete selectedTags[tag];
    } else {
      selectedTags[tag] = true;
    }
    this.setData({ selectedTags });
  },

  onCommentInput(e) {
    this.setData({ commentText: e.detail.value });
  },

  toggleAnonymous() {
    this.setData({ isAnonymous: !this.data.isAnonymous });
  },

  async submitFeedback() {
    const user = api.getUserInfo();
    if (!user) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    const userId = user.userId || user.id;
    const { orderId, selectedRating, selectedTags, commentText, isAnonymous } = this.data;

    if (!commentText.trim()) {
      wx.showToast({ title: '请输入评价内容', icon: 'none' });
      return;
    }

    try {
      const res = await api.post('/feedbacks', {
        userId: userId,
        orderId: orderId ? parseInt(orderId) : null,
        dishId: null,
        rating: selectedRating,
        content: commentText.trim(),
        tags: Object.keys(selectedTags),
        isAnonymous: isAnonymous,
        images: []
      });
      if (res.code === 200) {
        wx.showToast({ title: '评价提交成功', icon: 'success' });
        setTimeout(() => {
          wx.switchTab({ url: '/pages/orders/orders' });
        }, 1000);
      } else {
        wx.showToast({ title: res.message || '提交失败', icon: 'none' });
      }
    } catch (e) {
      wx.showToast({ title: '提交失败，请稍后重试', icon: 'none' });
    }
  }
});
