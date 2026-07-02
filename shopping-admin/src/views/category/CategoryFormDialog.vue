<template>
  <el-dialog :title="isEdit ? '编辑分类' : '新增分类'" :model-value="visible" width="450px" @close="$emit('update:visible', false)" @closed="resetForm">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="名称" prop="name"><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="父分类">
        <el-tree-select v-model="form.parentId" :data="parentOptions" :props="{label:'name',value:'id',children:'children'}" placeholder="无（一级分类）" clearable check-strictly style="width:100%" />
      </el-form-item>
      <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
      <el-form-item label="图标"><el-input v-model="form.icon" placeholder="图标URL" /></el-form-item>
      <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
    </el-form>
    <template #footer><el-button @click="$emit('update:visible', false)">取消</el-button><el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button></template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { createCategory, updateCategory, getCategoryTree } from '@/api/category'
import { ElMessage } from 'element-plus'

const props = defineProps({ visible: Boolean, editData: Object })
const emit = defineEmits(['update:visible', 'success'])
const isEdit = computed(() => !!props.editData?.id)

const formRef = ref(null)
const submitting = ref(false)
const parentOptions = ref([])
const form = reactive({ name: '', parentId: null, sort: 0, icon: '', status: 1 })
const rules = { name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }] }

watch(() => props.visible, async (v) => {
  if (v) {
    const res = await getCategoryTree(); parentOptions.value = res.data || []
    if (isEdit.value) {
      Object.assign(form, { name: props.editData.name, parentId: props.editData.parentId, sort: props.editData.sort || 0, icon: props.editData.icon || '', status: props.editData.status })
    }
  }
})

function resetForm() { Object.assign(form, { name: '', parentId: null, sort: 0, icon: '', status: 1 }); formRef.value?.resetFields() }

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const data = { name: form.name, parentId: form.parentId, sort: form.sort, icon: form.icon }
    if (isEdit.value) { await updateCategory(props.editData.id, data) }
    else { await createCategory(data) }
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    emit('update:visible', false); emit('success')
  } catch {} finally { submitting.value = false }
}
</script>
