<template>
  <div class="dashboard-view">
    <div v-loading="loading">
      <div v-if="!loading">
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon" style="background: rgba(64, 158, 255, 0.1); color: #409eff;">
              <el-icon :size="24"><User /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.totalUsers || 0 }}</p>
              <p class="stat-label">总用户数</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: rgba(103, 194, 58, 0.1); color: #67c23a;">
              <el-icon :size="24"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.totalManga || 0 }}</p>
              <p class="stat-label">漫画资源</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: rgba(230, 162, 60, 0.1); color: #e6a23c;">
              <el-icon :size="24"><Star /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.totalScores || 0 }}</p>
              <p class="stat-label">评分记录</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: rgba(144, 130, 247, 0.1); color: #9082f7;">
              <el-icon :size="24"><View /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.todayReadCount || 0 }}</p>
              <p class="stat-label">今日阅读</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: rgba(245, 108, 108, 0.1); color: #f56c6c;">
              <el-icon :size="24"><Collection /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.totalCollections || 0 }}</p>
              <p class="stat-label">收藏总数</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: rgba(0, 210, 190, 0.1); color: #00d2be;">
              <el-icon :size="24"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.totalPosts || 0 }}</p>
              <p class="stat-label">论坛帖子</p>
            </div>
          </div>
        </div>

        <!-- 数据概览图表区域 -->
        <div class="chart-section">
          <h3 class="section-title">数据概览</h3>
          <div class="chart-grid">
            <div class="chart-card">
              <h4 class="chart-title">漫画分类分布</h4>
              <div class="bar-chart">
                <div class="bar-item" v-for="item in categoryData" :key="item.name">
                  <span class="bar-label">{{ item.name }}</span>
                  <div class="bar-track">
                    <div class="bar-fill" :style="{ width: item.percent + '%' }"></div>
                  </div>
                  <span class="bar-value">{{ item.count }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Reading, Star, View, Collection, ChatDotRound } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const stats = ref({})

const categoryData = ref([])

async function fetchStats() {
  loading.value = true
  try {
    const res = await request.get('/admin/dashboard/stats')
    stats.value = res.data || {}
    // 如果后端返回了分类数据，则使用后端数据
    if (res.data && res.data.categoryStats) {
      const maxCount = Math.max(...res.data.categoryStats.map(c => c.count), 1)
      categoryData.value = res.data.categoryStats.map(c => ({
        name: c.name,
        count: c.count,
        percent: Math.round((c.count / maxCount) * 100)
      }))
    }
  } catch (e) {
    ElMessage.error('获取统计数据失败，请刷新重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.dashboard-view {
  padding: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform var(--transition-normal), box-shadow var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.stat-info {
  min-width: 0;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-top: 2px;
}

/* 图表区域 */
.chart-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 20px 0;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.chart-card {
  background: white;
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
}

.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 20px 0;
}

.bar-chart {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.bar-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bar-label {
  width: 48px;
  font-size: 13px;
  color: var(--color-text-secondary);
  text-align: right;
  flex-shrink: 0;
}

.bar-track {
  flex: 1;
  height: 20px;
  background: var(--color-bg-page);
  border-radius: 10px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary), var(--color-primary-light));
  border-radius: 10px;
  transition: width 0.6s ease;
  min-width: 4px;
}

.bar-value {
  width: 36px;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-primary);
  text-align: right;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 12px;
  }

  .stat-card {
    padding: 16px;
    gap: 12px;
  }

  .stat-icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
  }

  .stat-value {
    font-size: 22px;
  }

  .chart-grid {
    grid-template-columns: 1fr;
  }
}
</style>
