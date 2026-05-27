/**
 * 校园智慧食堂管理系统 - 前端 API 助手
 * 所有 HTML 页面共用此脚本连接后端
 */
(function () {
  const API_BASE = 'http://localhost:8080/api';

  window.API = {
    base: API_BASE,

    // Token 管理
    getToken() {
      return sessionStorage.getItem('token');
    },
    setToken(token) {
      sessionStorage.setItem('token', token);
    },
    clearToken() {
      sessionStorage.removeItem('token');
      sessionStorage.removeItem('user');
    },

    // 用户信息
    getUser() {
      const u = sessionStorage.getItem('user');
      return u ? JSON.parse(u) : null;
    },
    setUser(user) {
      sessionStorage.setItem('user', JSON.stringify(user));
    },

    // 通用请求
    async request(method, path, data) {
      const headers = { 'Content-Type': 'application/json' };
      const token = this.getToken();
      if (token) headers['Authorization'] = 'Bearer ' + token;

      const opts = { method, headers };
      if (data && method !== 'GET') {
        opts.body = JSON.stringify(data);
      }

      const url = API_BASE + path;
      const res = await fetch(url, opts);
      return res.json();
    },

    get(path) { return this.request('GET', path); },
    post(path, data) { return this.request('POST', path, data); },
    put(path, data) { return this.request('PUT', path, data); },
    del(path) { return this.request('DELETE', path); },

    // 检查登录状态，未登录跳转
    requireAuth() {
      const user = this.getUser();
      if (!user || !this.getToken()) {
        const role = this.guessRole();
        const loginPages = {
          STUDENT: 'student-login.html',
          CANTEEN_MANAGER: 'canteen-login.html',
          SUPERVISOR: 'logistics-login.html',
          SUPER_ADMIN: 'logistics-login.html'
        };
        const current = window.location.pathname.split('/').pop();
        const loginPage = loginPages[role] || 'student-login.html';
        if (!current.includes('login')) {
          window.location.href = loginPage;
        }
        return false;
      }
      return true;
    },

    guessRole() {
      const page = window.location.pathname.split('/').pop();
      if (page.startsWith('canteen-')) return 'CANTEEN_MANAGER';
      if (page.startsWith('logistics-')) return 'SUPERVISOR';
      return 'STUDENT';
    },

    logout() {
      this.clearToken();
      window.location.href = this.guessRole() === 'STUDENT'
        ? 'student-login.html'
        : this.guessRole() === 'CANTEEN_MANAGER'
          ? 'canteen-login.html'
          : 'logistics-login.html';
    }
  };
})();
