const api = require('../../utils/api');

Page({
  data: {
    username: '',
    password: '',
    remember: true,
    loading: false
  },

  onLoad() {
    const savedUser = wx.getStorageSync('savedUser');
    if (savedUser) {
      this.setData({ username: savedUser.username || '' });
    }
  },

  onUsernameInput(e) { this.setData({ username: e.detail.value }); },
  onPasswordInput(e) { this.setData({ password: e.detail.value }); },
  toggleRemember() { this.setData({ remember: !this.data.remember }); },

  async doLogin() {
    const { username, password, remember } = this.data;
    if (!username || !password) {
      wx.showToast({ title: '请输入账号和密码', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    try {
      const res = await api.post('/auth/login', { username, password });
      if (res.code === 200) {
        api.setToken(res.data.token);
        api.setUserInfo(res.data);
        if (remember) {
          wx.setStorageSync('savedUser', { username });
        } else {
          wx.removeStorageSync('savedUser');
        }
        wx.switchTab({ url: '/pages/index/index' });
      } else {
        wx.showToast({ title: res.message || '登录失败', icon: 'none' });
      }
    } catch (e) {
      wx.showToast({ title: '网络错误，请检查服务', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  }

});