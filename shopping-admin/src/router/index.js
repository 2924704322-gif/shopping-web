import { createRouter, createWebHashHistory } from 'vue-router'
import { getToken, getUserInfo } from '@/utils/token'
import AdminLayout from '@/layouts/AdminLayout.vue'

const routes = [{
  path: '/', component: AdminLayout,
  children: [
    { path: '', name: 'Dashboard', component: () => import('@/views/dashboard/DashboardView.vue'), meta: { title: '仪表盘' } },
    { path: 'shop', name: 'ShopView', component: () => import('@/views/shop/ShopView.vue'), meta: { title: '商品浏览' } },
    { path: 'cart', name: 'CartView', component: () => import('@/views/cart/CartView.vue'), meta: { title: '购物车' } },
    { path: 'users', name: 'UserList', component: () => import('@/views/user/UserListView.vue'), meta: { title: '用户管理', requiresAdmin: true } },
    { path: 'categories', name: 'CategoryList', component: () => import('@/views/category/CategoryListView.vue'), meta: { title: '分类管理', requiresAdmin: true } },
    { path: 'products', name: 'ProductList', component: () => import('@/views/product/ProductListView.vue'), meta: { title: '商品管理', requiresAdmin: true } },
    { path: 'email', name: 'EmailManage', component: () => import('@/views/email/EmailManage.vue'), meta: { title: '邮件管理', requiresAdmin: true } },
  ]
}]

const router = createRouter({ history: createWebHashHistory(), routes })

router.beforeEach((to, from, next) => {
  if (!getToken()) { window.location.href = '/login.html'; return }
  const userInfo = getUserInfo()
  const isAdmin = userInfo?.roles?.includes('ROLE_ADMIN')
  if (to.path === '/') { next(isAdmin ? '/users' : '/shop'); return }
  if (to.meta.requiresAdmin) {
    if (!isAdmin) { next('/shop'); return }
  }
  next()
})

export default router
