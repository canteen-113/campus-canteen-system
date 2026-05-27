/**
 * 校园智慧食堂管理系统 - 前端 API 助手
 * 所有 HTML 页面共用此脚本连接后端
 */
(function () {
  const API_BASE = 'http://localhost:8080/api';

  window.API = {
    base: API_BASE,

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

    getUser() {
      const u = sessionStorage.getItem('user');
      return u ? JSON.parse(u) : null;
    },
    setUser(user) {
      sessionStorage.setItem('user', JSON.stringify(user));
    },

    async request(method, path, data) {
      const headers = { 'Content-Type': 'application/json' };
      const token = this.getToken();
      if (token) headers['Authorization'] = 'Bearer ' + token;
      const opts = { method, headers };
      if (data && method !== 'GET') { opts.body = JSON.stringify(data); }
      const url = API_BASE + path;
      const res = await fetch(url, opts);
      return res.json();
    },

    get(path) { return this.request('GET', path); },
    post(path, data) { return this.request('POST', path, data); },
    put(path, data) { return this.request('PUT', path, data); },
    del(path) { return this.request('DELETE', path); },

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
        if (!current.includes('login')) {
          window.location.href = loginPages[role] || 'student-login.html';
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
      var page = window.location.pathname.split('/').pop();
      this.clearToken();
      if (page.startsWith('logistics-')) { window.location.href = 'logistics-login.html'; }
      else if (page.startsWith('canteen-')) { window.location.href = 'canteen-login.html'; }
      else { window.location.href = 'login.html'; }
    }
  };

  // 页面加载后自动更新侧边栏：显示当前登录用户名 + 角色
  document.addEventListener('DOMContentLoaded', function () {
    var user = window.API.getUser();
    if (!user || !user.name) return;

    var roleMap = {
      'SUPER_ADMIN': '超级管理员',
      'SUPERVISOR': '后勤处领导',
      'CANTEEN_MANAGER': '食堂经理',
      'STUDENT': '学生',
      'TEACHER': '教师',
      'STAFF': '教职工'
    };
    var roleName = roleMap[user.role] || user.role || '';
    var initial = (user.name || '?')[0];

    // 1. 更新侧边栏中用户卡片的名字和副标题
    var aside = document.querySelector('aside');
    if (aside) {
      // 找到 class 含 "text-white font-medium" 的 p 元素（用户名所在）
      var nameEls = aside.querySelectorAll('p.text-white');
      for (var i = 0; i < nameEls.length; i++) {
        var el = nameEls[i];
        var txt = el.textContent.trim();
        // 只要不是侧边栏主标题（智慧食堂），就替换为用户名
        if (txt.length <= 10 && txt.indexOf('智慧食堂') === -1) {
          el.textContent = user.name;
        }
      }
      // 找到用户名下方的小字（日期/Supervisor等），替换为角色名
      var subEls = aside.querySelectorAll('p.text-slate-500, p[class*="text-slate-500"], p[class*="text-slate-400"]');
      for (var j = 0; j < subEls.length; j++) {
        var sub = subEls[j];
        var subTxt = sub.textContent.trim();
        // 匹配日期格式或英文角色名
        if (/^\d{4}-\d{2}-\d{2}$/.test(subTxt) || subTxt === 'Supervisor' || subTxt === '2026-04-30') {
          sub.textContent = roleName;
        }
      }
    }

    // 2. 更新侧边栏底部的版本信息为用户名+角色
    var allDivs = document.querySelectorAll('aside div');
    for (var d = 0; d < allDivs.length; d++) {
      var div = allDivs[d];
      var text = div.textContent || '';
      if (text.indexOf('v3.5') !== -1 && div.children.length <= 1) {
        div.innerHTML =
          '<div style="display:flex;align-items:center;gap:8px;justify-content:center;">' +
          '<span style="width:24px;height:24px;border-radius:50%;background:#2563eb;display:flex;align-items:center;justify-content:center;color:#fff;font-size:11px;font-weight:700;">' + initial + '</span>' +
          '<span style="color:#cbd5e1;font-size:11px;">' + user.name + '</span>' +
          '</div>' +
          '<div style="color:#64748b;font-size:10px;margin-top:4px;text-align:center;">' + roleName + ' · v3.5.0</div>';
        break;
      }
    }
  });
})();
