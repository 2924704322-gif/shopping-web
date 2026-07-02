import { createRouter, createWebHashHistory } from 'vue-router'
import { getToken } from '@/utils/token'
import AdminLayout from '@/layouts/AdminLayout.vue'

const routes = [{
  path: '/', component: AdminLayout,
  children: [
    { path: '', name: 'Dashboard', component: () => import('@/views/dashboard/DashboardView.vue'), meta: { title: '仪表盘' } },
    { path: 'users', name: 'UserList', component: () => import('@/views/user/UserListView.vue'), meta: { title: '用户管理' } },
    { path: 'categories', name: 'CategoryList', component: () => import('@/views/category/CategoryListView.vue'), meta: { title: '分类管理' } },
    { path: 'products', name: 'ProductList', component: () => import('@/views/product/ProductListView.vue'), meta: { title: '商品管理' } },
  ]
}]

const router = createRouter({ history: createWebHashHistory(), routes })

router.beforeEach((to, from, next) => {
  if (!getToken()) { window.location.href = '/login.html'; return }
  next()
})

export default router
