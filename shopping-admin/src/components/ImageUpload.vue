<template>
  <div>
    <el-upload
      :action="uploadUrl"
      :headers="uploadHeaders"
      :on-success="handleSuccess"
      :on-error="handleError"
      :before-upload="beforeUpload"
      :file-list="fileList"
      :list-type="multiple ? 'picture-card' : 'picture-card'"
      :limit="limit"
      :on-remove="handleRemove"
      :multiple="multiple"
    >
      <el-icon><Plus /></el-icon>
    </el-upload>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { getToken } from '@/utils/token'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: { type: [String, Array], default: '' },
  multiple: { type: Boolean, default: false },
  limit: { type: Number, default: 10 },
})

const emit = defineEmits(['update:modelValue'])

const uploadUrl = '/api/upload/image'
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${getToken()}` }))

const fileList = computed(() => {
  if (!props.modelValue) return []
  const urls = props.multiple ? props.modelValue : [props.modelValue]
  return urls.filter(Boolean).map((url, i) => ({ uid: i, name: `image-${i}`, url, status: 'success' }))
})

function handleSuccess(res, file) {
  if (res.code === 200) {
    const url = res.data.url
    if (props.multiple) {
      emit('update:modelValue', [...(props.modelValue || []), url])
    } else {
      emit('update:modelValue', url)
    }
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

function handleError() { ElMessage.error('上传失败') }

function beforeUpload(file) {
  const allowed = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!allowed.includes(file.type)) { ElMessage.error('仅支持 JPG/PNG/GIF/WebP 格式'); return false }
  if (file.size > 10 * 1024 * 1024) { ElMessage.error('文件大小不能超过 10MB'); return false }
  return true
}

function handleRemove(file) {
  const url = file.url || file.response?.data?.url
  if (props.multiple) {
    emit('update:modelValue', (props.modelValue || []).filter(u => u !== url))
  } else {
    emit('update:modelValue', '')
  }
}
</script>
