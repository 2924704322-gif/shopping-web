<template>
  <div>
    <h3>🛒 购物车</h3>
    <div style="margin:12px 0;display:flex;justify-content:space-between">
      <el-checkbox v-model="allChecked" @change="toggleAll" :indeterminate="indeterminate">全选</el-checkbox>
      <el-button text type="danger" @click="handleClear">清空购物车</el-button>
    </div>

    <el-table :data="list" v-loading="loading" @selection-change="onSelectChange" ref="tableRef" row-key="id">
      <el-table-column type="selection" width="50" />
      <el-table-column label="商品" min-width="300">
        <template #default="{row}"><div style="display:flex;align-items:center;gap:12px"><el-image :src="row.mainImage" style="width:60px;height:60px" fit="cover" /><div><div style="font-weight:bold">{{ row.productName }}</div><div style="color:#f56c6c">¥{{ row.price }}</div></div></div></template>
      </el-table-column>
      <el-table-column label="数量" width="160">
        <template #default="{row}"><el-input-number v-model="row.quantity" :min="1" :max="999" size="small" @change="(v)=>handleQty(row.id,v)" /></template>
      </el-table-column>
      <el-table-column label="小计" width="100"><template #default="{row}">¥{{ (row.price*row.quantity).toFixed(2) }}</template></el-table-column>
      <el-table-column label="操作" width="80"><template #default="{row}"><el-button text type="danger" @click="handleRemove(row.id)">删除</el-button></template></el-table-column>
    </el-table>

    <div v-if="list.length===0&&!loading" style="text-align:center;padding:80px;color:#999">购物车为空，去<a href="#/shop" style="color:#409eff">逛逛</a>吧</div>

    <div v-if="list.length>0" style="margin-top:16px;display:flex;justify-content:flex-end;align-items:center;gap:16px;padding:12px 20px;background:#fff;border-top:2px solid #f56c6c">
      <span>已选 <b>{{ selectedCount }}</b> 件，合计：<b style="color:#f56c6c;font-size:22px">¥{{ totalPrice.toFixed(2) }}</b></span>
      <el-button type="danger" size="large" disabled>去结算</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getCartList, updateCartQty, updateCartCheck, removeCartItem, clearCart } from '@/api/cart'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const selected = ref([])
const tableRef = ref(null)

const allChecked = computed(() => list.value.length>0 && selected.value.length===list.value.length)
const indeterminate = computed(() => selected.value.length>0 && selected.value.length<list.value.length)
const selectedCount = computed(() => selected.value.reduce((s,i)=>s+i.quantity,0))
const totalPrice = computed(() => selected.value.reduce((s,i)=>s+i.price*i.quantity,0))

function onSelectChange(v) { selected.value = v }

async function loadData() {
  loading.value = true
  try {
    const res = await getCartList()
    list.value = res.data || []
    nextTick(() => list.value.forEach(item => { if (item.checked===1) tableRef.value?.toggleRowSelection(item, true) }))
  } catch {} finally { loading.value = false }
}

async function toggleAll(v) {
  if (v) {
    list.value.forEach(async row => {
      if (!selected.value.find(s=>s.id===row.id)) {
        tableRef.value?.toggleRowSelection(row, true)
      }
      if (row.checked !== 1) { try { await updateCartCheck(row.id, 1) } catch {} }
    })
  } else {
    tableRef.value?.clearSelection()
    list.value.forEach(async row => { if (row.checked !== 0) try { await updateCartCheck(row.id, 0) } catch {} })
  }
}

async function handleQty(id, qty) { try { await updateCartQty(id, qty) } catch {} }
async function handleRemove(id) {
  try { await removeCartItem(id); ElMessage.success('已移除'); loadData() } catch {}
}
async function handleClear() {
  try {
    await ElMessageBox.confirm('确定清空购物车？', '确认', { type: 'warning' })
    await clearCart(); ElMessage.success('已清空'); loadData()
  } catch {}
}

import { nextTick } from 'vue'
onMounted(loadData)
</script>
