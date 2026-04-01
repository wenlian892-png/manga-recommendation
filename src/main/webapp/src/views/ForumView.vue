<template>
  <div class="forum-view">
    <div class="page-header">
      <h1>交流论坛</h1>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索帖子..."
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
        <el-select v-model="sortBy" class="sort-select" @change="handleSortChange">
          <el-option label="最新发布" value="latest" />
          <el-option label="最多浏览" value="views" />
        </el-select>
        <el-button type="primary" @click="showAddDialog = true">发布帖子</el-button>
      </div>
    </div>

    <div v-if="loading">
      <PageSkeleton type="list" :count="5" />
    </div>
    <div v-else>
      <div v-if="list.length === 0"><EmptyState title="暂无帖子" /></div>
      <div v-else class="post-list">
        <div v-for="item in list" :key="item.id" class="post-item" @click="goDetail(item.id)">
          <h3 class="post-title">{{ item.title }}</h3>
          <p class="post-summary" v-if="item.content">{{ truncateText(item.content, 100) }}</p>
          <div class="post-meta">
            <span>作者: {{ item.username }}</span>
            <span>浏览: {{ item.viewCount || 0 }}</span>
            <span>回复: {{ item.replyCount || 0 }}</span>
            <span>时间: {{ formatRelativeTime(item.createTime) }}</span>
          </div>
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
        @size-change="handleSizeChange"
        @current-change="fetchList"
      />
    </div>

    <el-dialog v-model="showAddDialog" title="发布帖子" width="500px">
      <el-form :model="postForm" label-width="60px">
        <el-form-item label="标题">
          <el-input v-model="postForm.title" placeholder="请输入标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="5"
            placeholder="请输入内容"
            maxlength="2000"
            show-word-limit
          />
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
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import PageSkeleton from '@/components/PageSkeleton.vue'
import EmptyState from '@/components/EmptyState.vue'
import { formatRelativeTime, truncateText } from '@/utils/helpers'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const showAddDialog = ref(false)
const submitting = ref(false)
const list = ref([])
const searchKeyword = ref('')
const sortBy = ref('latest')
const pagination = reactive({ current: 1, size: 10, total: 0 })
const postForm = reactive({ title: '', content: '' })

async function fetchList() {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size
    }
    if (searchKeyword.value.trim()) {
      params.title = searchKeyword.value.trim()
    }
    if (sortBy.value === 'views') {
      params.sortBy = 'viewCount'
      params.order = 'desc'
    }
    const res = await request.get('/forum/list', { params })
    list.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (e) {
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.current = 1
  fetchList()
}

function handleSizeChange() {
  pagination.current = 1
  fetchList()
}

function handleSortChange() {
  pagination.current = 1
  fetchList()
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

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.forum-view {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: var(--spacing-xl);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.page-header h1 {
  font-size: var(--font-size-title);
  color: var(--color-text-primary);
}

.header-actions {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
}

.search-input {
  width: 240px;
}

.sort-select {
  width: 140px;
}

.post-list {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.post-item {
  padding: var(--spacing-xl);
  cursor: pointer;
  border-bottom: 1px solid var(--color-border-light);
  transition: background var(--transition-normal);
}

.post-item:hover {
  background: var(--color-bg-secondary);
}

.post-item:last-child {
  border-bottom: none;
}

.post-title {
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
  margin-bottom: 8px;
}

.post-summary {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-sm);
  line-height: 1.6;
}

.post-meta {
  display: flex;
  gap: var(--spacing-lg);
  font-size: 13px;
  color: var(--color-text-secondary);
}

.pagination {
  margin-top: var(--spacing-xl);
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .header-actions {
    flex-wrap: wrap;
    width: 100%;
  }

  .search-input {
    flex: 1;
    width: auto !important;
  }

  .post-meta {
    flex-wrap: wrap;
    gap: var(--spacing-sm);
  }

  .post-title {
    font-size: var(--font-size-base);
  }
}
</style>
