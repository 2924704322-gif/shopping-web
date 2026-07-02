<template>
  <el-dialog title="分配角色" :model-value="visible" width="400px" @close="$emit('update:visible', false)">
    <el-checkbox-group v-model="checkedRoles">
      <el-checkbox v-for="role in allRoles" :key="role.id" :label="role.id" :value="role.id">{{ role.roleName }}</el-checkbox>
    </el-checkbox-group>
    <template #footer><el-button @click="$emit('update:visible', false)">取消</el-button><el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button></template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getAllRoles } from '@/api/role'
import { assignUserRoles } from '@/api/user'
import { ElMessage } from 'element-plus'

const props = defineProps({ visible: Boolean, user: Object })
const emit = defineEmits(['update:visible', 'success'])
const allRoles = ref([])
const checkedRoles = ref([])
const submitting = ref(false)

watch(() => props.visible, async (v) => {
  if (v) {
    try { const res = await getAllRoles(); allRoles.value = res.data } catch {}
    checkedRoles.value = (props.user?.roles || []).map(code => allRoles.value.find(r => r.roleCode === code)?.id).filter(Boolean)
  }
})

async function handleSubmit() {
  submitting.value = true
  try {
    await assignUserRoles(props.user.id, checkedRoles.value)
    ElMessage.success('角色分配成功')
    emit('update:visible', false)
    emit('success')
  } catch {} finally { submitting.value = false }
}
</script>
