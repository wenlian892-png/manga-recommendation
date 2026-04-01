<template>
  <div class="forum-detail-view">
    <div v-if="loading">
      <PageSkeleton type="detail" />
    </div>
    <template v-else>
      <div v-if="!detail"><EmptyState title="帖子不存在" /></div>
      <template v-else>
        <div class="post-section">
          <h1 class="post-title">{{ detail.post.title }}</h1>
          <div class="post-meta">
            <span>作者: {{ detail.post.username }}</span>
            <span>浏览: {{ detail.post.viewCount }}</span>
            <span>时间: {{ formatDateTime(detail.post.createTime) }}</span>
          </div>
          <div class="post-content">{{ detail.post.content }}</div>
        </div>

        <div class="reply-section">
          <h3>回复 ({{ detail.replies?.length || 0 }})</h3>
          <div class="reply-list">
            <div v-for="reply in detail.replies" :key="reply.id" class="reply-item">
              <div class="reply-meta">
                <span class="reply-user">{{ reply.username }}</span>
                <span class="reply-time">{{ formatDateTime(reply.createTime) }}</span>
              </div>
              <div class="reply-content">{{ reply.content }}</div>
            </div>
          </div>

          <div class="reply-input">
            <el-input
              v-model="replyContent"
              type="textarea"
              :rows="3"
              placeholder="写下你的回复..."
              maxlength="1000"
              show-word-limit
            />
            <div class="reply-actions">
              <span class="char-count">{{ replyContent.length }} / 1000</span>
              <el-button type="primary" :loading="submitting" @click="submitReply">发布回复</el-button>
            </div>
          </div>
        </div>
      </template>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import PageSkeleton from '@/components/PageSkeleton.vue'
import EmptyState from '@/components/EmptyState.vue'
import { formatDateTime } from '@/utils/helpers'

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

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.forum-detail-view {
  max-width: 900px;
  margin: 0 auto;
  padding: var(--spacing-xl);
}

.post-section {
  background: var(--color-bg-white);
  padding: 30px;
  border-radius: var(--radius-lg);
  margin-bottom: var(--spacing-xl);
  box-shadow: var(--shadow-sm);
}

.post-title {
  font-size: var(--font-size-title);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-md);
}

.post-meta {
  display: flex;
  gap: var(--spacing-lg);
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--color-border-light);
}

.post-content {
  font-size: var(--font-size-md);
  line-height: var(--line-height-relaxed, 1.8);
  color: var(--color-text-primary);
  white-space: pre-wrap;
}

.reply-section {
  background: var(--color-bg-white);
  padding: var(--spacing-xl);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.reply-section h3 {
  margin-bottom: 20px;
  color: var(--color-text-primary);
}

.reply-list {
  margin-bottom: var(--spacing-xl);
}

.reply-item {
  padding: var(--spacing-md) 0;
  border-bottom: 1px solid var(--color-border-light);
}

.reply-meta {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: 8px;
}

.reply-user {
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
}

.reply-time {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

.reply-content {
  font-size: var(--font-size-sm);
  line-height: 1.6;
  color: var(--color-text-primary);
}

.reply-input {
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-border-light);
}

.reply-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--spacing-md);
}

.char-count {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

@media (max-width: 768px) {
  .post-title {
    font-size: var(--font-size-xl);
  }

  .post-meta {
    flex-wrap: wrap;
    gap: var(--spacing-sm);
  }

  .post-content {
    font-size: var(--font-size-base);
  }
}
</style>
