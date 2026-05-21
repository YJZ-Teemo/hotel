import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
});
/*
// --- 请求拦截器：发请求之前做点什么 ---
api.interceptors.request.use(
  config => {
    // 比如：每次请求都自动带上本地存好的 Token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = token;
    }
    return config; // 必须返回 config，否则请求发不出去
  },
  error => {
    return Promise.reject(error);
  }
);

// --- 响应拦截器：收到响应后做点什么 ---
api.interceptors.response.use(
  response => {
    // 比如：后端返回 {status: '500', msg: '系统错误'}
    // 这里可以直接统一弹窗提示，不用在每个组件里都写一遍
    if (response.data.status !== '200') {
       showNotify({ type: 'danger', message: response.data.msg || '系统错误' });
    }
    return response; 
  },
  error => {
    // 比如：断网了，或者服务器崩了（500）
    showNotify({ type: 'danger', message: '服务器连接异常，请稍后重试' });
    return Promise.reject(error);
  }
);
*/

export default api;
