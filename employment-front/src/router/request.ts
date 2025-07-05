// src/utils/request.ts
import axios from 'axios';

// 从环境变量获取基础URL，如果没有设置则使用默认值
const baseURL ='http://localhost:9908/api';

// 创建 axios 实例
const service = axios.create({
  baseURL, // 这里应该是字符串，所以需要用引号包裹
  timeout: 10000, // 请求超时时间
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 这里可以添加 token 等认证信息
    const token = localStorage.getItem('access_token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 对响应数据做处理
    return response.data;
  },
  error => {
    // 处理 HTTP 错误
    let errorMessage = '请求失败';

    if (error.response) {
      // 服务器返回了错误状态码
      switch (error.response.status) {
        case 400:
          errorMessage = '请求参数错误';
          break;
        case 401:
          errorMessage = '未授权，请重新登录';
          // 可以在这里触发重新登录逻辑
          break;
        case 403:
          errorMessage = '拒绝访问';
          break;
        case 404:
          errorMessage = '请求资源不存在';
          break;
        case 500:
          errorMessage = '服务器错误';
          break;
        default:
          errorMessage = `服务器错误: ${error.response.status}`;
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      errorMessage = '网络错误，请检查您的连接';
    } else {
      // 请求设置错误
      errorMessage = error.message;
    }

    // 显示错误提示（这里使用 console.error，实际项目中可以使用 UI 提示）
    console.error(errorMessage);

    return Promise.reject(error);
  }
);

export default service;
