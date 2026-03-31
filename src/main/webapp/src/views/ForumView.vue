<template>
  <div class="forum-view">
    <div class="page-header">
      <h1>交流论坛</h1>
      <el-button type="primary" @click="showAddDialog = true">发布帖子</el-button>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="list.length === 0" class="empty">暂无帖子</div>
    <div v-else class="post-list">
      <div v-for="item in list" :key="item.id" class="post-item" @click="goDetail(item.id)">
        <h3 class="post-title">{{ item.title }}</h3>
        <div class="post-meta">
          <span>作者: {{ item.username }}</span>
          <span>浏览: {{ item.viewCount }}</span>
          <span>时间: {{ formatTime(item.createTime) }}</span>
        </div>
      </div>
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchList"
        @current-change="fetchList"
      />
    </div>

    <el-dialog v-model="showAddDialog" title="发布帖子" width="500px">
      <el-form :model="postForm" label-width="60px">
        <el-form-item label="标题">
          <el-input v-model="postForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="postForm.content" type="textarea" :rows="5" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitPost">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const showAddDialog = ref(false)
const submitting = ref(false)
const list = ref([])
const pagination = reactive({ current: 1, size: 10, total: 0 })
const postForm = reactive({ title: '', content: '' })

async function fetchList() {
  loading.value = true
  try {
    const res = await request.get('/forum/list', {
      params: { current: pagination.current, size: pagination.size }
    })
    list.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  router.push({ name: 'ForumDetail', params: { id } })
}

async function submitPost() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  if (!postForm.title || !postForm.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  submitting.value = true
  try {
    await request.post('/forum/add', postForm)
    ElMessage.success('发布成功')
    showAddDialog.value = false
    postForm.title = ''
    postForm.content = ''
    fetchList()
  } catch (e) {
  } finally {
    submitting.value = false
  }
}

function formatTime(time) {
  if (!time) return ''
  return new Date(time).toLocaleDateString()
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.forum-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
}

.loading, .empty {
  text-align: center;
  padding: 60px;
  color: #999;
}

.post-list {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.post-item {
  padding: 20px;
  cursor: pointer;
  border-bottom: 1px solid #eee;
  transition: background 0.3s;
}

.post-item:hover {
  background: #f9f9f9;
}

.post-item:last-child {
  border-bottom: none;
}

.post-title {
  font-size: 18px;
  margin-bottom: 12px;
}

.post-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #999;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
