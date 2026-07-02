import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export function usePage({ fetchFn, deleteFn, statusFn, defaultQuery = {} }) {
  const loading = ref(false)
  const list = ref([])
  const total = ref(0)

  const query = reactive({
    pageNum: 1,
    pageSize: 10,
    keyword: '',
    ...defaultQuery
  })

  async function loadData() {
    loading.value = true
    try {
      const res = await fetchFn(query)
      list.value = res.data.records || []
      total.value = res.data.total || 0
    } catch {} finally { loading.value = false }
  }

  function handleSearch() { query.pageNum = 1; loadData() }
  function handleReset() {
    Object.assign(query, { pageNum: 1, pageSize: 10, keyword: '', ...defaultQuery })
    loadData()
  }
  function handlePageChange(p) { query.pageNum = p; loadData() }
  function handleSizeChange(s) { query.pageSize = s; query.pageNum = 1; loadData() }

  async function handleDelete(id) {
    try {
      await ElMessageBox.confirm('确定删除该记录吗？', '确认删除', { type: 'warning' })
      await deleteFn(id)
      ElMessage.success('删除成功')
      loadData()
    } catch {}
  }

  async function handleToggleStatus(id, currentStatus, label = '状态') {
    const newStatus = currentStatus === 1 ? 0 : 1
    await statusFn(id, newStatus)
    ElMessage.success(`${label}已${newStatus === 1 ? '启用' : '禁用'}`)
    loadData()
  }

  return { loading, list, total, query, loadData, handleSearch, handleReset,
           handlePageChange, handleSizeChange, handleDelete, handleToggleStatus }
}
