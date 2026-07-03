import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken, removeUserInfo } from '@/utils/token'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

// 请求拦截器：自动附加 Token
request.interceptors.request.use(config => {
  const token = getToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
}, error => Promise.reject(error))

// 响应拦截器：统一错误处理
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      if (res.code === 401) {
        removeToken(); removeUserInfo()
        window.location.href = '/login.html'
      }
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    if (error.response) {
      const status = error.response.status
      if (status === 401 || status === 403) {
        removeToken(); removeUserInfo()
        window.location.href = '/login.html'
      }
      ElMessage.error(error.response.data?.message || `请求失败 (${status})`)
    } else {
      ElMessage.error('网络错误，请检查连接')
    }
    return Promise.reject(error)
  }
)

export default request
