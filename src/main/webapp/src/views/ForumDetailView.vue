<template>
  <div class="forum-detail-view">
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="!detail" class="empty">帖子不存在</div>
    <template v-else>
      <div class="post-section">
        <h1 class="post-title">{{ detail.post.title }}</h1>
        <div class="post-meta">
          <span>作者: {{ detail.post.username }}</span>
          <span>浏览: {{ detail.post.viewCount }}</span>
          <span>时间: {{ formatTime(detail.post.createTime) }}</span>
        </div>
        <div class="post-content">{{ detail.post.content }}</div>
      </div>

      <div class="reply-section">
        <h3>回复 ({{ detail.replies?.length || 0 }})</h3>
        <div class="reply-list">
          <div v-for="reply in detail.replies" :key="reply.id" class="reply-item">
            <div class="reply-meta">
              <span class="reply-user">{{ reply.username }}</span>
              <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
            </div>
            <div class="reply-content">{{ reply.content }}</div>
          </div>
        </div>

        <div class="reply-input">
          <el-input v-model="replyContent" type="textarea" :rows="3" placeholder="写下你的回复..." />
          <el-button type="primary" style="margin-top: 12px;" :loading="submitting" @click="submitReply">发布回复</el-button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const detail = ref(null)
const replyContent = ref('')
const submitting = ref(false)

async function fetchDetail() {
  loading.value = true
  try {
    const res = await request.get(`/forum/detail/${route.params.id}`)
    detail.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function submitReply() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  submitting.value = true
  try {
    await request.post('/forum/reply', {
      postId: route.params.id,
      content: replyContent.value
    })
    ElMessage.success('回复成功')
    replyContent.value = ''
    fetchDetail()
  } catch (e) {
  } finally {
    submitting.value = false
  }
}

function formatTime(time) {
  if (!time) return ''
  return new Date(time).toLocaleString()
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.forum-detail-view {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.loading, .empty {
  text-align: center;
  padding: 60px;
  color: #999;
}

.post-section {
  background: white;
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 24px;
}

.post-title {
  font-size: 28px;
  margin-bottom: 16px;
}

.post-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #999;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.post-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
}

.reply-section {
  background: white;
  padding: 24px;
  border-radius: 12px;
}

.reply-section h3 {
  margin-bottom: 20px;
}

.reply-list {
  margin-bottom: 24px;
}

.reply-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.reply-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}

.reply-user {
  font-weight: bold;
  color: #409eff;
}

.reply-time {
  font-size: 12px;
  color: #999;
}

.reply-content {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}

.reply-input {
  padding-top: 16px;
  border-top: 1px solid #eee;
}
</style>
