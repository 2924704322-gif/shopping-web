import request from './request'

export function getAllRoles() {
  return request({ url: '/admin/roles', method: 'get' })
}
