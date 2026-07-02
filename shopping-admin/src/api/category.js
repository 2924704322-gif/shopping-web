import request from './request'

export function getCategoryTree() {
  return request({ url: '/categories/tree', method: 'get' })
}
export function getCategoryPage(params) {
  return request({ url: '/admin/categories', method: 'get', params })
}
export function getCategoryDetail(id) {
  return request({ url: `/admin/categories/${id}`, method: 'get' })
}
export function createCategory(data) {
  return request({ url: '/admin/categories', method: 'post', data })
}
export function updateCategory(id, data) {
  return request({ url: `/admin/categories/${id}`, method: 'put', data })
}
export function deleteCategory(id) {
  return request({ url: `/admin/categories/${id}`, method: 'delete' })
}
export function setCategoryStatus(id, status) {
  return request({ url: `/admin/categories/${id}/status`, method: 'put', data: { status } })
}
