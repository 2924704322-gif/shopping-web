<template>
  <div>
    <h3>数据库浏览</h3>
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane label="用户表 (user)" name="user" />
      <el-tab-pane label="角色表 (role)" name="role" />
      <el-tab-pane label="用户角色 (user_role)" name="userRole" />
      <el-tab-pane label="分类表 (category)" name="category" />
      <el-tab-pane label="商品表 (product)" name="product" />
    </el-tabs>

    <el-table :data="list" v-loading="loading" border stripe max-height="600" style="margin-top:12px">
      <el-table-column v-for="col in columns" :key="col.prop" :prop="col.prop" :label="col.label" :width="col.width" show-overflow-tooltip />
    </el-table>

    <el-pagination v-if="paged" style="margin-top:16px;justify-content:flex-end" background layout="total, prev, pager, next" :total="total" :current-page="pageNum" :page-size="pageSize" @current-change="loadCurrent" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getUserPage } from '@/api/user'
import { getCategoryPage } from '@/api/category'
import { getAdminProductPage } from '@/api/product'
import request from '@/api/request'

const activeTab = ref('user')
const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const paged = ref(true)
const columns = ref([])

const colMaps = {
  user: [
    { prop:'id',label:'ID',width:60 },{ prop:'username',label:'用户名',width:120 },
    { prop:'nickname',label:'昵称',width:100 },{ prop:'email',label:'邮箱',width:180 },
    { prop:'phone',label:'手机号',width:130 },{ prop:'gender',label:'性别',width:60 },
    { prop:'birthday',label:'生日',width:110 },{ prop:'status',label:'状态',width:60 },
    { prop:'lastLoginTime',label:'最后登录',width:160 },{ prop:'lastLoginIp',label:'登录IP',width:130 },
    { prop:'createTime',label:'创建时间',width:160 },{ prop:'updateTime',label:'更新时间',width:160 },
    { prop:'deleted',label:'已删除',width:70 },
  ],
  role: [
    { prop:'id',label:'ID',width:60 },{ prop:'roleName',label:'角色名',width:120 },
    { prop:'roleCode',label:'角色编码',width:140 },{ prop:'description',label:'描述',width:200 },
    { prop:'status',label:'状态',width:60 },{ prop:'sort',label:'排序',width:60 },
    { prop:'createTime',label:'创建时间',width:160 },{ prop:'deleted',label:'已删除',width:70 },
  ],
  userRole: [
    { prop:'id',label:'ID',width:60 },{ prop:'userId',label:'用户ID',width:80 },
    { prop:'roleId',label:'角色ID',width:80 },{ prop:'createTime',label:'创建时间',width:160 },
    { prop:'updateTime',label:'更新时间',width:160 },
  ],
  category: [
    { prop:'id',label:'ID',width:60 },{ prop:'name',label:'名称',width:140 },
    { prop:'parentId',label:'父级ID',width:80 },{ prop:'level',label:'层级',width:60 },
    { prop:'sort',label:'排序',width:60 },{ prop:'icon',label:'图标',width:200 },
    { prop:'status',label:'状态',width:60 },{ prop:'createTime',label:'创建时间',width:160 },
    { prop:'updateTime',label:'更新时间',width:160 },{ prop:'deleted',label:'已删除',width:70 },
  ],
  product: [
    { prop:'id',label:'ID',width:60 },{ prop:'name',label:'名称',width:180 },
    { prop:'categoryId',label:'分类ID',width:70 },{ prop:'price',label:'售价',width:90 },
    { prop:'originalPrice',label:'原价',width:90 },{ prop:'stock',label:'库存',width:70 },
    { prop:'sales',label:'销量',width:70 },{ prop:'mainImage',label:'主图',width:200 },
    { prop:'unit',label:'单位',width:60 },{ prop:'status',label:'上下架',width:70 },
    { prop:'sort',label:'排序',width:60 },{ prop:'createTime',label:'创建时间',width:160 },
    { prop:'updateTime',label:'更新时间',width:160 },{ prop:'deleted',label:'已删除',width:70 },
  ],
}

const loaders = {
  user:     async () => { const r = await getUserPage({pageNum:pageNum.value,pageSize:pageSize.value}); list.value=r.data.records; total.value=r.data.total },
  role:     async () => { const r = await request({url:'/admin/roles',method:'get'}); list.value=r.data; total.value=r.data.length; paged.value=false },
  userRole: async () => { const r = await request({url:'/admin/data/user-role',method:'get',params:{pageNum:pageNum.value,pageSize:pageSize.value}}); list.value=r.data.records; total.value=r.data.total },
  category: async () => { const r = await getCategoryPage({pageNum:pageNum.value,pageSize:pageSize.value}); list.value=r.data.records; total.value=r.data.total },
  product:  async () => { const r = await getAdminProductPage({pageNum:pageNum.value,pageSize:pageSize.value}); list.value=r.data.records; total.value=r.data.total },
}

async function onTabChange(name) {
  columns.value = colMaps[name]
  paged.value = (name !== 'role')
  pageNum.value = 1
  loading.value = true
  try { await loaders[name]() } catch {} finally { loading.value = false }
}
async function loadCurrent(p) { pageNum.value = p; await loaders[activeTab.value]() }

// init
columns.value = colMaps.user
</script>
