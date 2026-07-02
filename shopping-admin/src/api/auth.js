import request from './request'

export function loginApi(data) {
  return request({ url: '/auth/login', method: 'post', data })
}

export function logoutApi() {
  return request({ url: '/auth/logout', method: 'post' })
}
