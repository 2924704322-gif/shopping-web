<template>
  <div>
    <h3>🛍 商品浏览</h3>
    <el-form :inline="true" style="margin:16px 0">
      <el-form-item><el-input v-model="query.keyword" placeholder="搜索商品..." clearable style="width:200px" /></el-form-item>
      <el-form-item><el-tree-select v-model="query.categoryId" :data="categories" :props="{label:'name',value:'id',children:'children'}" placeholder="全部分类" clearable check-strictly style="width:160px" /></el-form-item>
      <el-form-item>价格：<el-input-number v-model="query.priceMin" placeholder="最低" :min="0" style="width:110px" /> ~ <el-input-number v-model="query.priceMax" placeholder="最高" :min="0" style="width:110px" /></el-form-item>
      <el-form-item><el-select v-model="sortField" placeholder="排序" style="width:120px"><el-option label="默认" value="" /><el-option label="价格升序" value="price_asc" /><el-option label="价格降序" value="price_desc" /><el-option label="销量" value="sales" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" @click="search">搜索</el-button><el-button @click="reset">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="16" v-loading="loading">
      <el-col :span="6" v-for="item in list" :key="item.id" style="margin-bottom:16px">
        <el-card shadow="hover" :body-style="{padding:'12px'}" style="cursor:pointer">
          <el-image :src="item.mainImage" fit="cover" style="width:100%;height:180px" @click="openDetail(item)"><template #error><div style="font-size:60px;text-align:center;line-height:180px">📦</div></template></el-image>
          <div style="margin-top:8px;font-size:14px;font-weight:bold;overflow:hidden;text-overflow:ellipsis;white-space:nowrap" @click="openDetail(item)">{{ item.name }}</div>
          <div style="color:#999;font-size:12px;margin:4px 0">{{ item.categoryName }}</div>
          <div style="display:flex;align-items:center;justify-content:space-between">
            <span style="color:#f56c6c;font-size:18px;font-weight:bold">¥{{ item.price }}</span>
            <span style="color:#999;font-size:12px">已售{{ item.sales }}</span>
          </div>
          <el-button type="danger" size="small" style="width:100%;margin-top:8px" @click="handleAddCart(item)">加入购物车</el-button>
        </el-card>
      </el-col>
    </el-row>

    <el-pagination v-if="total>0" style="margin-top:16px;justify-content:center" background layout="total, prev, pager, next" :total="total" :page-size="12" v-model:current-page="pageNum" @current-change="loadData" />

    <el-dialog v-model="detailVisible" title="商品详情" width="600px">
      <template v-if="detail">
        <div style="display:flex;gap:20px">
          <el-image :src="detail.mainImage" style="width:280px;height:280px" fit="cover" />
          <div style="flex:1">
            <h2>{{ detail.name }}</h2>
            <p style="color:#999">{{ detail.categoryName }}</p>
            <p style="color:#f56c6c;font-size:28px;font-weight:bold">¥{{ detail.price }} <span style="font-size:14px;color:#999;text-decoration:line-through" v-if="detail.originalPrice">¥{{ detail.originalPrice }}</span></p>
            <p>库存：{{ detail.stock }} | 销量：{{ detail.sales }} | 单位：{{ detail.unit }}</p>
            <p style="white-space:pre-wrap;color:#666;margin-top:12px">{{ detail.description }}</p>
            <div v-if="detail.subImages?.length" style="display:flex;gap:8px;margin-top:8px"><el-image v-for="(img,i) in detail.subImages" :key="i" :src="img" style="width:60px;height:60px" fit="cover" /></div>
          </div>
        </div>
      </template>
      <template #footer><el-button @click="detailVisible=false">关闭</el-button><el-button type="danger" @click="handleAddCart(detail)">加入购物车</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCategoryTree } from '@/api/category'
import request from '@/api/request'
import { addToCart } from '@/api/cart'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const categories = ref([])
const query = reactive({ keyword: '', categoryId: null, priceMin: null, priceMax: null })
const sortField = ref('')
const detailVisible = ref(false)
const detail = ref(null)

async function loadData() {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: 12, ...query }
    if (sortField.value === 'price_asc') { params.sortField = 'price'; params.asc = true }
    else if (sortField.value === 'price_desc') { params.sortField = 'price'; params.asc = false }
    else if (sortField.value === 'sales') { params.sortField = 'sales' }
    const res = await request({ url: '/products', method: 'get', params })
    list.value = res.data.records; total.value = res.data.total
  } catch {} finally { loading.value = false }
}

function search() { pageNum.value = 1; loadData() }
function reset() { Object.assign(query, { keyword: '', categoryId: null, priceMin: null, priceMax: null }); sortField.value = ''; search() }

async function openDetail(item) {
  try {
    const res = await request({ url: `/products/${item.id}`, method: 'get' })
    detail.value = res.data; detailVisible.value = true
  } catch {}
}

async function handleAddCart(item) {
  try {
    await addToCart({ productId: item.id, quantity: 1 })
    ElMessage.success('已加入购物车')
  } catch {}
}

onMounted(async () => {
  try { const res = await getCategoryTree(); categories.value = res.data || [] } catch {}
  loadData()
})
</script>
