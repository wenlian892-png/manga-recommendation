<template>
  <div class="manga-manage-view">
    <div class="toolbar">
      <el-input v-model="filters.title" placeholder="搜索标题" style="width: 200px;" />
      <el-input v-model="filters.author" placeholder="搜索作者" style="width: 200px;" />
      <el-button type="primary" @click="fetchList">搜索</el-button>
      <el-button type="success" @click="showAddDialog = true">新增漫画</el-button>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <el-table v-else :data="list" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <img :src="row.coverUrl || '/placeholder.png'" class="table-cover" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="clickCount" label="点击" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          {{ row.status === 1 ? '连载' : '完结' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="ADMIN_PAGE_SIZES"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next"
        @current-change="fetchList"
        @size-change="handleSizeChange"
      />
    </div>

    <el-dialog v-model="showAddDialog" :title="editId ? '编辑漫画' : '新增漫画'" width="500px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="form.author" />
        </el-form-item>
        <el-form-item label="封面URL">
          <el-input v-model="form.coverUrl" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%;">
            <el-option v-for="cat in MANGA_CATEGORIES" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%;">
            <el-option label="连载" :value="1" />
            <el-option label="完结" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { MANGA_CATEGORIES, ADMIN_PAGE_SIZES } from '@/utils/constants'
import request from '@/utils/request'

const loading = ref(false)
const list = ref([])
const showAddDialog = ref(false)
const editId = ref(null)
const formRef = ref(null)
const filters = reactive({ title: '', author: '' })
const pagination = reactive({ current: 1, size: 10, total: 0 })
const form = reactive({ title: '', author: '', coverUrl: '', category: '', status: 0, description: '' })

const formRules = {
  title: [
    { required: true, message: '请输入漫画标题', trigger: 'blur' }
  ]
}

function handleSizeChange() {
  pagination.current = 1
  fetchList()
}

async function fetchList() {
  loading.value = true
  try {
    const res = await request.get('/admin/manga/page', {
      params: { current: pagination.current, size: pagination.size, title: filters.title, author: filters.author }
    })
    list.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function handleEdit(row) {
  editId.value = row.id
  Object.assign(form, { title: row.title, author: row.author, coverUrl: row.coverUrl, category: row.category, status: row.status, description: row.description })
  showAddDialog.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }

  try {
    if (editId.value) {
      await request.put(`/admin/manga/update/${editId.value}`, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/admin/manga/add', form)
      ElMessage.success('新增成功')
    }
    showAddDialog.value = false
    editId.value = null
    Object.keys(form).forEach(k => form[k] = '')
    form.status = 0
    fetchList()
  } catch (e) {
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确认删除?', '警告', { type: 'warning' })
    await request.delete(`/admin/manga/delete/${id}`)
    ElMessage.success('删除成功')
    fetchList()
  } catch (e) {
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.manga-manage-view {
  padding: 0;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.loading {
  text-align: center;
  padding: 60px;
  color: var(--color-text-secondary);
}

.table-cover {
  width: 48px;
  height: 64px;
  object-fit: cover;
  border-radius: var(--radius-sm);
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
