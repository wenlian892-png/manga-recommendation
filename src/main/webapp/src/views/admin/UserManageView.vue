<template>
  <div class="user-manage-view">
    <h1>用户管理</h1>
    <div class="toolbar">
      <el-input v-model="filters.username" placeholder="搜索用户名" style="width: 200px;" />
      <el-button type="primary" @click="fetchList">搜索</el-button>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <el-table v-else :data="list" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column label="角色" width="100">
        <template #default="{ row }">
          {{ row.role === 0 ? '普通用户' : '管理员' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          {{ row.status === 0 ? '正常' : '已封禁' }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" size="small" type="danger" @click="handleBan(row.id)">封禁</el-button>
          <span v-else style="color: #999;">已封禁</span>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="total, prev, pager, next"
        @current-change="fetchList"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const list = ref([])
const filters = reactive({ username: '' })
const pagination = reactive({ current: 1, size: 10, total: 0 })

async function fetchList() {
  loading.value = true
  try {
    const res = await request.get('/admin/user/page', {
      params: { current: pagination.current, size: pagination.size, username: filters.username }
    })
    list.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handleBan(id) {
  try {
    await ElMessageBox.confirm('确认封禁该用户?', '警告', { type: 'warning' })
    await request.post(`/admin/user/ban/${id}`)
    ElMessage.success('封禁成功')
    fetchList()
  } catch (e) {
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.user-manage-view {
  padding: 0 10px;
}

h1 {
  font-size: 24px;
  margin-bottom: 24px;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.loading {
  text-align: center;
  padding: 60px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
