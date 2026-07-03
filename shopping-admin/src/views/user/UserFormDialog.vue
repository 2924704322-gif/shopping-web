<template>
  <el-dialog :title="isEdit ? '编辑用户' : '新增用户'" :model-value="visible" width="500px" @close="$emit('update:visible', false)" @closed="resetForm">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="用户名" prop="username" v-if="!isEdit">
        <el-input v-model="form.username" placeholder="3-50字符" />
      </el-form-item>
      <el-form-item label="密码" prop="password" v-if="!isEdit">
        <el-input v-model="form.password" type="password" placeholder="至少6位" show-password />
      </el-form-item>
      <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
      <el-form-item label="邮箱" prop="email"><el-input v-model="form.email" /></el-form-item>
      <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" /></el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.gender">
          <el-radio :value="0">未知</el-radio><el-radio :value="1">男</el-radio><el-radio :value="2">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="生日"><el-date-picker v-model="form.birthday" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
      <el-form-item label="状态" v-if="!isEdit">
        <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
      </el-form-item>
    </el-form>
    <template #footer><el-button @click="$emit('update:visible', false)">取消</el-button><el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button></template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { createUser, updateUser, getUserDetail } from '@/api/user'
import { ElMessage } from 'element-plus'

const props = defineProps({ visible: Boolean, editData: Object })
const emit = defineEmits(['update:visible', 'success'])
const isEdit = computed(() => !!props.editData?.id)

const formRef = ref(null)
const submitting = ref(false)
const form = reactive({ username: '', password: '', nickname: '', email: '', phone: '', gender: 0, birthday: null, status: 1 })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 50, message: '3-50字符', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '至少6位', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
}

watch(() => props.visible, async (v) => {
  if (v && isEdit.value) {
    try {
      const res = await getUserDetail(props.editData.id)
      Object.assign(form, { username: res.data.username, nickname: res.data.nickname || '', email: res.data.email || '', phone: res.data.phone || '', gender: res.data.gender ?? 0, birthday: res.data.birthday || '' })
    } catch {}
  }
})

function resetForm() {
  Object.assign(form, { username: '', password: '', nickname: '', email: '', phone: '', gender: 0, birthday: null, status: 1 })
  formRef.value?.resetFields()
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateUser(props.editData.id, { nickname: form.nickname, email: form.email || '', phone: form.phone || '', gender: form.gender, birthday: form.birthday || null })
    } else {
      await createUser({ ...form, birthday: form.birthday || null, email: form.email || null, phone: form.phone || null })
    }
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    emit('update:visible', false)
    emit('success')
  } catch {} finally { submitting.value = false }
}
</script>
