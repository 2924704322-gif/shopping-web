<template>
  <el-container style="height:100vh">
    <el-aside width="220px" style="background:#1d1e2c;overflow-y:auto">
      <div style="color:#fff;text-align:center;padding:20px 0;font-size:18px;font-weight:bold;border-bottom:1px solid #333">
        🛒 购物平台管理
      </div>
      <el-menu :default-active="route.path" router background-color="#1d1e2c" text-color="#bfcbd9" active-text-color="#409eff" style="border:none">
        <el-menu-item index="/"><el-icon><DataAnalysis /></el-icon> 仪表盘</el-menu-item>
        <el-menu-item index="/users"><el-icon><User /></el-icon> 用户管理</el-menu-item>
        <el-menu-item index="/categories"><el-icon><FolderOpened /></el-icon> 分类管理</el-menu-item>
        <el-menu-item index="/products"><el-icon><Goods /></el-icon> 商品管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display:flex;align-items:center;justify-content:space-between;border-bottom:1px solid #eee;background:#fff">
        <span style="font-size:16px;font-weight:bold">{{ route.meta?.title || route.name }}</span>
        <el-dropdown @command="handleCommand">
          <span style="cursor:pointer;display:flex;align-items:center;gap:8px">
            <el-avatar :size="32" icon="UserFilled" />
            <span>{{ auth.userInfo?.nickname || auth.userInfo?.username }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main style="background:#f5f7fa">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
const route = useRoute()
const auth = useAuthStore()

function handleCommand(cmd) {
  if (cmd === 'logout') auth.logout()
}
</script>
