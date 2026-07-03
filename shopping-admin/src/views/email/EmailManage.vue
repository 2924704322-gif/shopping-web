<template>
  <div>
    <h3>📧 邮件管理</h3>
    <el-row :gutter="20" style="margin-top:16px">
      <el-col :span="12">
        <el-card header="📋 今日报表预览" shadow="hover">
          <div v-if="preview" style="white-space:pre-wrap;font-family:monospace;background:#f5f7fa;padding:16px;border-radius:4px;min-height:200px;max-height:500px;overflow-y:auto;font-size:14px;line-height:1.8">{{ preview.content }}</div>
          <div v-else v-loading="loading" style="min-height:200px"></div>
          <template #footer>
            收件人：<el-tag>{{ preview?.to || '—' }}</el-tag>
            &nbsp;主题：<el-tag>{{ preview?.subject || '—' }}</el-tag>
            <el-button style="float:right" @click="loadPreview">刷新预览</el-button>
          </template>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="📤 发送邮件" shadow="hover">
          <div style="display:flex;flex-direction:column;align-items:center;justify-content:center;min-height:200px">
            <el-icon style="font-size:80px;color:#409eff;margin-bottom:20px"><Message /></el-icon>
            <p style="color:#666;margin-bottom:20px">将以上报表立即发送到收件人邮箱</p>
            <el-button type="primary" size="large" :loading="sending" @click="handleSend">立即发送</el-button>
          </div>
          <template #footer>
            <span v-if="sendResult" :style="{color:sendResult.includes('成功')?'#67c23a':'#f56c6c'}">{{ sendResult }}</span>
            <span v-else style="color:#999">上次发送状态：—</span>
          </template>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const sending = ref(false)
const preview = ref(null)
const sendResult = ref('')

async function loadPreview() {
  loading.value = true
  try {
    const res = await request({ url: '/admin/email/preview', method: 'get' })
    preview.value = res.data
  } catch {} finally { loading.value = false }
}

async function handleSend() {
  sending.value = true
  try {
    const res = await request({ url: '/admin/email/send-report', method: 'post' })
    sendResult.value = res.message
    ElMessage.success('发送成功')
  } catch (e) {
    sendResult.value = '发送失败'
  } finally { sending.value = false }
}

onMounted(loadPreview)
</script>
