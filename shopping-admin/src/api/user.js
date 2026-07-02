import request from './request'

export function getUserPage(params) {
  return request({ url: '/admin/users', method: 'get', params })
}
export function getUserDetail(id) {
  return request({ url: `/admin/users/${id}`, method: 'get' })
}
export function createUser(data) {
  return request({ url: '/admin/users', method: 'post', data })
}
export function updateUser(id, data) {
  return request({ url: `/admin/users/${id}`, method: 'put', data })
}
export function deleteUser(id) {
  return request({ url: `/admin/users/${id}`, method: 'delete' })
}
export function setUserStatus(id, status) {
  return request({ url: `/admin/users/${id}/status`, method: 'put', data: { status } })
}
export function assignUserRoles(id, roleIds) {
  return request({ url: `/admin/users/${id}/roles`, method: 'put', data: roleIds })
}
