import request from './request'

export function getCartList()     { return request({ url: '/cart', method: 'get' }) }
export function addToCart(data)   { return request({ url: '/cart', method: 'post', data }) }
export function updateCartQty(id, quantity) { return request({ url: `/cart/${id}`, method: 'put', data: { quantity } }) }
export function updateCartCheck(id, checked) { return request({ url: `/cart/${id}/check`, method: 'put', data: { checked } }) }
export function removeCartItem(id) { return request({ url: `/cart/${id}`, method: 'delete' }) }
export function clearCart()       { return request({ url: '/cart/clear', method: 'delete' }) }
