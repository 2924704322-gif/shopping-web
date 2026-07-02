<template>
  <div>
    <h3>欢迎回来，{{ auth.userInfo?.nickname || auth.userInfo?.username }}</h3>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="6" v-for="card in cards" :key="card.title">
        <el-card shadow="hover" style="text-align:center;cursor:pointer" @click="$router.push(card.link)">
          <div style="font-size:40px">{{ card.icon }}</div>
          <div style="margin-top:10px;font-size:16px;color:#666">{{ card.title }}</div>
          <div style="font-size:28px;font-weight:bold;color:#409eff;margin-top:5px">{{ card.count }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { getUserPage } from '@/api/user'
import { getAdminProductPage } from '@/api/product'

const auth = useAuthStore()
const cards = ref([
  { icon: '👤', title: '用户总数', count: 0, link: '/users' },
  { icon: '📦', title: '商品总数', count: 0, link: '/products' },
  { icon: '✅', title: '上架商品', count: 0, link: '/products' },
  { icon: '📁', title: '快捷入口', count: '→', link: '/categories' },
])

onMounted(async () => {
  try {
    const [users, products, onShelf] = await Promise.all([
      getUserPage({ pageNum: 1, pageSize: 1 }),
      getAdminProductPage({ pageNum: 1, pageSize: 1 }),
      getAdminProductPage({ pageNum: 1, pageSize: 1, status: 1 }),
    ])
    cards.value[0].count = users.data.total
    cards.value[1].count = products.data.total
    cards.value[2].count = onShelf.data.total
  } catch {}
})
</script>
