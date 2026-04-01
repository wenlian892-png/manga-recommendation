<template>
  <div class="leaderboard-view">
    <h1>排行榜</h1>
    <div class="tabs">
      <el-radio-group v-model="activeTab">
        <el-radio-button value="hot">全站人气榜</el-radio-button>
        <el-radio-button value="score">高分神作榜</el-radio-button>
      </el-radio-group>
    </div>

    <div v-if="loading">
      <PageSkeleton type="list" :count="10" />
    </div>
    <div v-else-if="list.length === 0">
      <EmptyState title="暂无数据" />
    </div>
    <div v-else class="rank-list">
      <div v-for="(item, index) in list" :key="item.id" class="rank-item" :class="{ 'top-item': index < 3 }" @click="goDetail(item.id)">
        <div class="rank-num" :class="{ 'top-1': index === 0, 'top-2': index === 1, 'top-3': index === 2 }">
          <span v-if="index < 3" class="medal">{{ ['🥇', '🥈', '🥉'][index] }}</span>
          <span v-else>{{ index + 1 }}</span>
        </div>
        <img :src="item.coverUrl || '/placeholder.png'" class="rank-cover" />
        <div class="rank-info">
          <h3>{{ item.title }}</h3>
          <p>{{ item.author }}</p>
        </div>
        <div class="rank-score">
          <span v-if="activeTab === 'score'">{{ item.avgScore?.toFixed(1) || '暂无' }}</span>
          <span v-else>{{ item.clickCount }} 点击</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import PageSkeleton from '@/components/PageSkeleton.vue'
import EmptyState from '@/components/EmptyState.vue'

const router = useRouter()
const activeTab = ref('hot')
const loading = ref(false)
const list = ref([])

async function fetchList() {
  loading.value = true
  try {
    const res = await request.get('/manga/leaderboard', { params: { type: activeTab.value } })
    list.value = res.data || []
  } catch (e) {
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  router.push({ name: 'MangaDetail', params: { id } })
}

watch(activeTab, () => {
  fetchList()
})

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.leaderboard-view {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: var(--page-padding);
}

h1 {
  font-size: var(--font-size-hero);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-lg);
}

.tabs {
  margin-bottom: var(--spacing-lg);
}

.rank-list {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.rank-item {
  display: flex;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-lg);
  cursor: pointer;
  transition: background var(--transition-normal), transform var(--transition-fast);
  animation: fadeInLeft 0.3s ease forwards;
  opacity: 0;
}

.rank-item:nth-child(1) { animation-delay: 0.05s; }
.rank-item:nth-child(2) { animation-delay: 0.1s; }
.rank-item:nth-child(3) { animation-delay: 0.15s; }
.rank-item:nth-child(4) { animation-delay: 0.2s; }
.rank-item:nth-child(5) { animation-delay: 0.25s; }
.rank-item:nth-child(6) { animation-delay: 0.3s; }
.rank-item:nth-child(7) { animation-delay: 0.35s; }
.rank-item:nth-child(8) { animation-delay: 0.4s; }
.rank-item:nth-child(9) { animation-delay: 0.45s; }
.rank-item:nth-child(10) { animation-delay: 0.5s; }

@keyframes fadeInLeft {
  from { opacity: 0; transform: translateX(-10px); }
  to { opacity: 1; transform: translateX(0); }
}

.rank-item:hover {
  background: var(--color-bg-secondary);
  transform: translateX(2px);
}

.rank-num {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-placeholder);
  background: var(--color-bg-secondary);
  border-radius: 50%;
  margin-right: var(--spacing-md);
  flex-shrink: 0;
}

.rank-num.top-1 {
  background: linear-gradient(135deg, #ffd700, #ffb347);
  color: var(--color-bg-white);
  font-size: var(--font-size-xxl);
  box-shadow: 0 4px 12px rgba(255, 183, 71, 0.5);
}

.rank-num.top-2 {
  background: linear-gradient(135deg, #c0c0c0, #a0a0a0);
  color: var(--color-bg-white);
  font-size: var(--font-size-xxl);
  box-shadow: 0 4px 12px rgba(160, 160, 160, 0.5);
}

.rank-num.top-3 {
  background: linear-gradient(135deg, #cd7f32, #b87333);
  color: var(--color-bg-white);
  font-size: var(--font-size-xxl);
  box-shadow: 0 4px 12px rgba(184, 115, 51, 0.5);
}

.medal {
  font-size: 24px;
}

.rank-item.top-item {
  background: linear-gradient(90deg, rgba(255, 215, 0, 0.06) 0%, transparent 100%);
  border-left: 3px solid #ffd700;
}

.rank-cover {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  margin-right: var(--spacing-md);
}

.rank-info {
  flex: 1;
}

.rank-info h3 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-xs);
}

.rank-info p {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.rank-score {
  font-size: var(--font-size-lg);
  color: var(--color-danger);
  font-weight: var(--font-weight-bold);
}

@media (max-width: 768px) {
  .rank-item {
    padding: var(--spacing-md);
  }

  .rank-cover {
    width: 45px;
    height: 60px;
  }

  .rank-info h3 {
    font-size: var(--font-size-base);
  }

  .rank-score {
    font-size: var(--font-size-base);
  }
}
</style>
