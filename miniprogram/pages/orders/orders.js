const api = require('../../utils/api');

// 状态映射（ENUM ↔ 中文）
var STATUS_MAP = {
  'PENDING': '待取餐', 'PREPARING': '待取餐', 'READY': '待取餐',
  'PICKED_UP': '已完成', 'CANCELLED': '已取消'
};
// 中文 → ENUM（用于 API 筛选）
var STATUS_REVERSE = {
  '待取餐': 'PREPARING', '已完成': 'PICKED_UP', '已取消': 'CANCELLED'
};
var STATUS_COLOR = {
  '待取餐': { stColor: '#f97316', stBg: '#fff7ed' },
  '已完成': { stColor: '#059669', stBg: '#ecfdf5' },
  '已取消': { stColor: '#f87171', stBg: '#fef2f2' }
};

function mapColor(st) {
  var c = STATUS_COLOR[st] || {};
  return { stColor: c.stColor || '#94a3b8', stBg: c.stBg || '#f8fafc' };
}

function makeMock() {
  var raw = [
    { id:1, canteenName:'第一食堂', windowName:'3号窗', status:'待取餐', firstDishName:'秘制红烧牛腩', createTime:'2026-05-15 11:32', mealPeriod:'午餐', totalPrice:'14.00', totalQty:2, pickupCode:'A128' },
    { id:2, canteenName:'教工餐厅', windowName:'5号窗', status:'已完成', firstDishName:'清蒸鲈鱼片', createTime:'2026-05-14 12:15', mealPeriod:'午餐', totalPrice:'15.00', totalQty:1, pickupCode:'B205' },
    { id:3, canteenName:'第二食堂', windowName:'5号窗', status:'已完成', firstDishName:'鲜虾小馄饨', createTime:'2026-05-14 07:30', mealPeriod:'早餐', totalPrice:'6.00', totalQty:1, pickupCode:'C108' },
    { id:4, canteenName:'第一食堂', windowName:'2号窗', status:'已取消', firstDishName:'手撕包菜', createTime:'2026-05-13 17:45', mealPeriod:'晚餐', totalPrice:'4.50', totalQty:1, pickupCode:'D307' }
  ];
  var IMG = {
    '秘制红烧牛腩':'https://modao.cc/agent-py/media/generated_images/2026-04-30/0867b32ab91f46e6bebcc1e0774bd519.jpg',
    '清蒸鲈鱼片':'https://modao.cc/agent-py/media/generated_images/2026-04-30/8e50eb8d4716443684ccad60f9798a69.jpg',
    '鲜虾小馄饨':'https://modao.cc/agent-py/media/generated_images/2026-04-30/a62173a763ea4eba9c0861c91d34da2f.jpg',
    '手撕包菜':'https://modao.cc/agent-py/media/generated_images/2026-04-30/35b9a2fda2634e3791981bb293bd4fd4.jpg'
  };
  return raw.map(function(o) {
    var c = mapColor(o.status);
    o.stColor = c.stColor;
    o.stBg = c.stBg;
    o.firstImage = IMG[o.firstDishName] || '';
    return o;
  });
}
var MOCK_ORDERS = makeMock();

Page({
  data: {
    list: MOCK_ORDERS,
    activeTab: ''
  },

  onLoad() {
    this.doLoad();
  },

  onShow() {
    this.doLoad();
  },

  doLoad() {
    var user = api.getUserInfo();
    if (!user) { this.doFilter(MOCK_ORDERS); return; }
    var userId = user.userId || user.id;
    var tab = this.data.activeTab;
    var url = '/orders?userId=' + userId + '&page=1&size=20';
    if (tab) {
      var enumStatus = STATUS_REVERSE[tab] || tab;
      url = url + '&status=' + encodeURIComponent(enumStatus);
    }
    var that = this;
    api.get(url).then(function(res) {
      if (res.code === 200 && res.data) {
        var orders = Array.isArray(res.data) ? res.data : res.data.content || res.data.records || [];
        if (orders.length > 0) {
          var mapped = orders.map(function(o) {
            var items = o.items || o.orderItems || [];
            var first = items.length > 0 ? items[0] : {};
            var ds = STATUS_MAP[o.status] || o.status || '待取餐';
            var tq = items.reduce(function(s,i){return s+(i.quantity||1);},0);
            var tp = items.reduce(function(s,i){return s+(i.price||0)*(i.quantity||1);},0).toFixed(2);
            if (tp === '0.00') tp = Number(o.actualAmount || o.totalAmount || 0).toFixed(2);
            var color = mapColor(ds);
            return {
              id: o.id,
              canteenName: o.canteenName || '',
              windowName: o.windowName || (o.windowNo ? o.windowNo + '号窗' : ''),
              status: ds,
              stColor: color.stColor,
              stBg: color.stBg,
              firstDishName: first.dishName || '菜品',
              firstImage: first.image || '',
              createTime: o.createTime || '',
              mealPeriod: o.mealPeriod || '',
              totalPrice: tp,
              totalQty: tq || 1,
              pickupCode: o.pickupCode || o.pickup_code || '----'
            };
          });
          that.doFilter(mapped);
          return;
        }
      }
      that.doFilter(MOCK_ORDERS);
    }).catch(function(e) {
      console.warn('load fail', e);
      that.doFilter(MOCK_ORDERS);
    });
  },

  doFilter(arr) {
    var tab = this.data.activeTab;
    if (tab) arr = arr.filter(function(o) { return o.status === tab; });
    this.setData({ list: arr });
  },

  onTab(e) {
    var t = e.currentTarget.dataset.tab;
    this.setData({ activeTab: t }, function() { this.doLoad(); }.bind(this));
  },

  onCancel(e) {
    var id = e.currentTarget.dataset.id;
    var that = this;
    wx.showModal({
      title: '提示', content: '确定取消此预约吗？',
      success: function(res) {
        if (!res.confirm) return;
        api.put('/orders/' + id + '/cancel').then(function(r) {
          if (r.code === 200) { wx.showToast({ title: '已取消', icon: 'success' }); that.doLoad(); }
          else wx.showToast({ title: r.message || '失败', icon: 'none' });
        }).catch(function() { wx.showToast({ title: '失败', icon: 'none' }); });
      }
    });
  },

  onAgain(e) {
    var id = e.currentTarget.dataset.id;
    api.get('/orders/' + id).then(function(res) {
      if (res.code === 200 && res.data) {
        var items = res.data.items || res.data.orderItems || [];
        if (items.length > 0) {
          var cart = items.map(function(i) { return { id: i.dishId, name: i.dishName, price: i.price, quantity: i.quantity || 1, image: i.image || '', canteenName: res.data.canteenName || '', windowName: res.data.windowName || '' }; });
          wx.setStorageSync('cart', cart);
        }
      }
    }).catch(function() {});
    wx.navigateTo({ url: '/pages/reserve/reserve' });
  }
});
