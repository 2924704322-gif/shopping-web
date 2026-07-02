<template>
  <div>
    <el-form :inline="true" :model="query" size="default">
      <el-form-item><el-input v-model="query.keyword" placeholder="用户名/昵称/邮箱/手机号" clearable style="width:280px" /></el-form-item>
      <el-form-item><el-select v-model="query.status" placeholder="状态" clearable style="width:120px"><el-option label="启用" :value="1" /><el-option label="禁用" :value="0" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" @click="handleSearch">搜索</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      <el-form-item style="float:right"><el-button type="primary" @click="openCreate">＋ 新增用户</el-button></el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column label="状态" width="80"><template #default="{row}"><StatusTag :status="row.status" /></template></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          <el-button size="small" :type="row.status===1?'warning':'success'" @click="handleToggleStatus(row.id, row.status)">{{ row.status===1?'禁用':'启用' }}</el-button>
          <el-button size="small" @click="openRoleAssign(row)">角色</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination style="margin-top:16px;justify-content:flex-end" background layout="total, sizes, prev, pager, next" :total="total" :page-sizes="[10,20,50]" :current-page="query.pageNum" :page-size="query.pageSize" @current-change="handlePageChange" @size-change="handleSizeChange" />

    <UserFormDialog v-model:visible="dialogVisible" :edit-data="editData" @success="loadData" />
    <RoleAssignDialog v-model:visible="roleDialogVisible" :user="roleUser" @success="loadData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { usePage } from '@/composables/usePage'
import { getUserPage, deleteUser, setUserStatus } from '@/api/user'
import StatusTag from '@/components/StatusTag.vue'
import UserFormDialog from './UserFormDialog.vue'
import RoleAssignDialog from './RoleAssignDialog.vue'

const { loading, list, total, query, loadData, handleSearch, handleReset, handlePageChange, handleSizeChange, handleDelete, handleToggleStatus } = usePage({ fetchFn: getUserPage, deleteFn: deleteUser, statusFn: setUserStatus })

const dialogVisible = ref(false)
const editData = ref(null)
const roleDialogVisible = ref(false)
const roleUser = ref(null)

function openCreate() { editData.value = null; dialogVisible.value = true }
function openEdit(row) { editData.value = row; dialogVisible.value = true }
function openRoleAssign(row) { roleUser.value = row; roleDialogVisible.value = true }

onMounted(() => loadData())
</script>
