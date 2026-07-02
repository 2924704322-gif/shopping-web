<template>
  <div>
    <el-button type="primary" @click="openCreate" style="margin-bottom:16px">＋ 新增分类</el-button>

    <el-table :data="treeData" v-loading="loading" border stripe row-key="id" default-expand-all>
      <el-table-column prop="name" label="名称" min-width="200" />
      <el-table-column label="层级" width="80"><template #default="{row}"><el-tag :type="row.level===1?'primary':'success'" size="small">{{ row.level===1?'一级':'二级' }}</el-tag></template></el-table-column>
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="状态" width="80"><template #default="{row}"><StatusTag :status="row.status" /></template></el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          <el-button size="small" :type="row.status===1?'warning':'success'" @click="handleToggleStatus(row.id, row.status)">{{ row.status===1?'禁用':'启用' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <CategoryFormDialog v-model:visible="dialogVisible" :edit-data="editData" @success="loadData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategoryPage, deleteCategory, setCategoryStatus } from '@/api/category'
import StatusTag from '@/components/StatusTag.vue'
import CategoryFormDialog from './CategoryFormDialog.vue'

const loading = ref(false)
const flatList = ref([])
const dialogVisible = ref(false)
const editData = ref(null)

function buildTree(list) {
  const map = {}, roots = []
  list.forEach(item => { map[item.id] = { ...item, children: [] } })
  list.forEach(item => {
    if (item.parentId && map[item.parentId]) map[item.parentId].children.push(map[item.id])
    else if (!item.parentId) roots.push(map[item.id])
  })
  return roots
}
const treeData = ref([])

async function loadData() {
  loading.value = true
  try {
    const res = await getCategoryPage({ pageNum: 1, pageSize: 1000 })
    flatList.value = res.data.records || []
    treeData.value = buildTree(flatList.value)
  } catch {} finally { loading.value = false }
}

async function handleDelete(id) {
  const { ElMessageBox, ElMessage } = await import('element-plus')
  try {
    await ElMessageBox.confirm('确定删除该分类吗？', '确认删除', { type: 'warning' })
    await deleteCategory(id)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

async function handleToggleStatus(id, currentStatus) {
  const newStatus = currentStatus === 1 ? 0 : 1
  await setCategoryStatus(id, newStatus)
  const { ElMessage } = await import('element-plus')
  ElMessage.success(`状态已${newStatus === 1 ? '启用' : '禁用'}`)
  loadData()
}

function openCreate() { editData.value = null; dialogVisible.value = true }
function openEdit(row) { editData.value = row; dialogVisible.value = true }

onMounted(() => loadData())
</script>
