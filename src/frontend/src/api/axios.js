import axios from 'axios';
import { globalError } from '@/stores/errorStore'
import router from '@/router/index.js';

const _http = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,
});

_http.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token && token !== 'undefined' && token !== 'null') {
    config.headers.Authorization = `Bearer ${token}`
  }
    return config;
}); 

_http.interceptors.response.use(
  response => response,
  error => {
    if (!error.response) {
      // сервер не отвечает вообще
      // globalError.value = 'Сервер недоступен'
      router.push('/error')
    }

    const status = error.response.status

    if (status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }

    else if (status === 403) {
      // globalError.value = 'Нет доступа'
      router.push('/forbidden')
    }

    else if (status === 404) {
      router.push('/not-found')
    }

    else if (status === 500) {
        router.push('/error')
      // globalError.value = 'Ошибка сервера. Попробуйте позже'
    }

    else if (status === 400) {
      const message = error.response.data?.message || 'Ошибка запроса'
      globalError.value = message
    }

    return Promise.reject(error)
  }
)

export default _http;