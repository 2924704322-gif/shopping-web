<template>
  <div>
    <el-form :inline="true" :model="query" size="default">
      <el-form-item><el-input v-model="query.keyword" placeholder="商品名称" clearable style="width:180px" /></el-form-item>
      <el-form-item><el-tree-select v-model="query.categoryId" :data="categoryOptions" :props="{label:'name',value:'id',children:'children'}" placeholder="全部分类" clearable check-strictly style="width:160px" /></el-form-item>
      <el-form-item><el-input-number v-model="query.priceMin" placeholder="最低价" :min="0" style="width:130px" /></el-form-item>
      <el-form-item><el-input-number v-model="query.priceMax" placeholder="最高价" :min="0" style="width:130px" /></el-form-item>
      <el-form-item><el-select v-model="query.status" placeholder="状态" clearable style="width:100px"><el-option label="上架" :value="1" /><el-option label="下架" :value="0" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" @click="handleSearch">搜索</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      <el-form-item style="float:right"><el-button type="primary" @click="openCreate">＋ 新增商品</el-button></el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column label="图片" width="80"><template #default="{row}"><el-image :src="row.mainImage" style="width:50px;height:50px" fit="cover" v-if="row.mainImage"><template #error><span style="font-size:24px">📦</span></template></el-image><span v-else style="font-size:24px">📦</span></template></el-table-column>
      <el-table-column prop="name" label="名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column label="价格" width="120"><template #default="{row}">¥{{ row.price }}</template></el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column label="状态" width="80"><template #default="{row}"><StatusTag :status="row.status" on-text="上架" off-text="下架" /></template></el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          <el-button size="small" :type="row.status===1?'warning':'success'" @click="handleToggleStatus(row.id, row.status, '上下架')">{{ row.status===1?'下架':'上架' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination style="margin-top:16px;justify-content:flex-end" background layout="total, sizes, prev, pager, next" :total="total" :page-sizes="[10,20,50]" :current-page="query.pageNum" :page-size="query.pageSize" @current-change="handlePageChange" @size-change="handleSizeChange" />

    <ProductFormDialog v-model:visible="dialogVisible" :edit-data="editData" @success="loadData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { usePage } from '@/composables/usePage'
import { getAdminProductPage, deleteProduct, setProductStatus } from '@/api/product'
import { getCategoryTree } from '@/api/category'
import StatusTag from '@/components/StatusTag.vue'
import ProductFormDialog from './ProductFormDialog.vue'

const categoryOptions = ref([])

const { loading, list, total, query, loadData, handleSearch, handleReset, handlePageChange, handleSizeChange, handleDelete, handleToggleStatus } = usePage({
  fetchFn: getAdminProductPage,
  deleteFn: deleteProduct,
  statusFn: setProductStatus,
  defaultQuery: { priceMin: null, priceMax: null, categoryId: null }
})

const dialogVisible = ref(false)
const editData = ref(null)
function openCreate() { editData.value = null; dialogVisible.value = true }
function openEdit(row) { editData.value = row; dialogVisible.value = true }

onMounted(async () => {
  try { const res = await getCategoryTree(); categoryOptions.value = res.data || [] } catch {}
  loadData()
})
</script>
