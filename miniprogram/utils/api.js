const app = getApp();

const request = (method, path, data) => {
  return new Promise((resolve, reject) => {
    const token = app.globalData.token || wx.getStorageSync('token');
    const header = { 'Content-Type': 'application/json' };
    if (token) header['Authorization'] = 'Bearer ' + token;

    wx.request({
      url: app.globalData.apiBase + path,
      method: method,
      data: data,
      header: header,
      success(res) {
        if (res.statusCode === 200) {
          resolve(res.data);
        } else if (res.statusCode === 401) {
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          wx.reLaunch({ url: '/pages/login/login' });
          reject(new Error('未授权'));
        } else {
          resolve(res.data);
        }
      },
      fail(err) {
        wx.showToast({ title: '网络错误', icon: 'none' });
        reject(err);
      }
    });
  });
};

module.exports = {
  get: (path) => request('GET', path),
  post: (path, data) => request('POST', path, data),
  put: (path, data) => request('PUT', path, data),
  del: (path) => request('DELETE', path),
  setToken(token) {
    app.globalData.token = token;
    wx.setStorageSync('token', token);
  },
  setUserInfo(user) {
    app.globalData.userInfo = user;
    wx.setStorageSync('userInfo', user);
  },
  getUserInfo() {
    return app.globalData.userInfo || wx.getStorageSync('userInfo');
  },
  logout() {
    app.globalData.token = null;
    app.globalData.userInfo = null;
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
    wx.reLaunch({ url: '/pages/login/login' });
  }
};
