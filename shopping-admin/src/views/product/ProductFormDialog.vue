<template>
  <el-dialog :title="isEdit ? '编辑商品' : '新增商品'" :model-value="visible" width="600px" @close="$emit('update:visible', false)" @closed="resetForm">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="商品名称" prop="name"><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="所属分类" prop="categoryId">
        <el-tree-select v-model="form.categoryId" :data="categoryOptions" :props="{label:'name',value:'id',children:'children'}" placeholder="请选择分类" check-strictly style="width:100%" />
      </el-form-item>
      <el-form-item label="主图"><ImageUpload v-model="form.mainImage" /></el-form-item>
      <el-form-item label="副图"><ImageUpload v-model="form.subImages" multiple :limit="8" /></el-form-item>
      <el-row :gutter="16">
        <el-col :span="12"><el-form-item label="售价" prop="price"><el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="原价"><el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="8"><el-form-item label="库存" prop="stock"><el-input-number v-model="form.stock" :min="0" style="width:100%" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="单位"><el-input v-model="form.unit" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" style="width:100%" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item>
      <el-form-item label="上架"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
    </el-form>
    <template #footer><el-button @click="$emit('update:visible', false)">取消</el-button><el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button></template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { createProduct, updateProduct, getAdminProductDetail } from '@/api/product'
import { getCategoryTree } from '@/api/category'
import ImageUpload from '@/components/ImageUpload.vue'
import { ElMessage } from 'element-plus'

const props = defineProps({ visible: Boolean, editData: Object })
const emit = defineEmits(['update:visible', 'success'])
const isEdit = computed(() => !!props.editData?.id)

const formRef = ref(null)
const submitting = ref(false)
const categoryOptions = ref([])
const form = reactive({ name: '', categoryId: null, price: null, originalPrice: null, stock: 0, mainImage: '', subImages: [], unit: '件', sort: 0, description: '', status: 1 })
const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
}

watch(() => props.visible, async (v) => {
  if (v) {
    try { const res = await getCategoryTree(); categoryOptions.value = res.data || [] } catch {}
    if (isEdit.value) {
      try {
        const res = await getAdminProductDetail(props.editData.id)
        const d = res.data
        Object.assign(form, { name: d.name, categoryId: d.categoryId, price: d.price, originalPrice: d.originalPrice, stock: d.stock, mainImage: d.mainImage || '', subImages: d.subImages || [], unit: d.unit || '件', sort: d.sort || 0, description: d.description || '', status: d.status })
      } catch {}
    }
  }
})

function resetForm() {
  Object.assign(form, { name: '', categoryId: null, price: null, originalPrice: null, stock: 0, mainImage: '', subImages: [], unit: '件', sort: 0, description: '', status: 1 })
  formRef.value?.resetFields()
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const data = { ...form }
    if (isEdit.value) { await updateProduct(props.editData.id, data) }
    else { await createProduct(data) }
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    emit('update:visible', false); emit('success')
  } catch {} finally { submitting.value = false }
}
</script>
