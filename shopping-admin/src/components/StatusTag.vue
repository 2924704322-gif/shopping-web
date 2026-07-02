<template>
  <el-tag :type="status === 1 ? 'success' : 'info'" size="small">
    {{ status === 1 ? onText : offText }}
  </el-tag>
</template>

<script setup>
defineProps({
  status: { type: Number, required: true },
  onText: { type: String, default: '启用' },
  offText: { type: String, default: '禁用' },
})
</script>
