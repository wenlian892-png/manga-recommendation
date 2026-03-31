<template>
  <div class="category-view">
    <div class="page-header">
      <h1>漫画分类</h1>
      <div class="filters">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索漫画..."
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>
    </div>

    <div class="category-tabs">
      <div class="tab-scroll">
        <span
          class="tab-item"
          :class="{ active: selectedCategory === '' }"
          @click="selectCategory('')"
        >
          全部
        </span>
        <span
          v-for="cat in categoryOptions"
          :key="cat.value"
          class="tab-item"
          :class="{ active: selectedCategory === cat.value }"
          @click="selectCategory(cat.value)"
        >
          {{ cat.label }}
        </span>
      </div>
    </div>

    <div class="manga-count" v-if="!loading">
      共找到 <span class="count-num">{{ pagination.total }}</span> 部漫画
    </div>

    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      加载中...
    </div>
    <div v-else-if="list.length === 0" class="empty">
      <p>暂无数据</p>
      <el-button type="primary" plain @click="resetFilters">重置筛选</el-button>
    </div>
    <div v-else class="manga-grid">
      <div
        v-for="item in list"
        :key="item.id"
        class="manga-card"
        @click="goDetail(item.id)"
      >
        <div class="cover-wrapper">
          <img
            :src="item.coverUrl || '/placeholder.png'"
            class="manga-cover"
            loading="lazy"
          />
          <div class="cover-overlay">
            <span class="overlay-tag">{{ item.category }}</span>
          </div>
        </div>
        <div class="manga-info">
          <h3 class="manga-title">{{ item.title }}</h3>
          <p class="manga-author">{{ item.author }}</p>
          <div class="manga-meta">
            <span class="meta-item">
              <el-icon><View /></el-icon>
              {{ item.clickCount || 0 }}
            </span>
            <span class="meta-score" v-if="item.avgScore">
              <el-icon><Star /></el-icon>
              {{ item.avgScore.toFixed(1) }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-wrapper" v-if="pagination.total > 0">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, View, Star, Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

const COMMON_CATEGORIES = ['热血', '恋爱', '冒险', '悬疑', '运动']

const loading = ref(false)
const list = ref([])
const searchKeyword = ref('')
const selectedCategory = ref('')
const categoryOptions = COMMON_CATEGORIES.map(c => ({ label: c, value: c }))

const pagination = reactive({
  current: 1,
  size: 24,
  total: 0
})

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
    if (selectedCategory.value) {
      params.category = selectedCategory.value
    }
    const res = await request.get('/manga/page', { params })
    list.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function selectCategory(category) {
  if (selectedCategory.value === category) return
  selectedCategory.value = category
  pagination.current = 1
  fetchList()
}

function handleSearch() {
  pagination.current = 1
  fetchList()
}

function resetFilters() {
  searchKeyword.value = ''
  selectedCategory.value = ''
  pagination.current = 1
  fetchList()
}

function handleSizeChange() {
  pagination.current = 1
  fetchList()
}

function handleCurrentChange() {
  fetchList()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function goDetail(id) {
  router.push({ name: 'MangaDetail', params: { id } })
}

onMounted(() => {
  if (route.query.keyword) {
    searchKeyword.value = route.query.keyword
  }
  fetchList()
})
</script>

<style scoped>
.category-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 28px;
  color: #333;
}

.filters {
  display: flex;
  gap: 12px;
}

.search-input {
  width: 280px;
}

.category-tabs {
  background: white;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.tab-scroll {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tab-item {
  padding: 8px 18px;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  background: #f5f5f5;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.tab-item:hover {
  background: #ecf5ff;
  color: #409eff;
}

.tab-item.active {
  background: #409eff;
  color: white;
  border-color: #409eff;
}

.manga-count {
  font-size: 14px;
  color: #999;
  margin-bottom: 16px;
  padding: 0 4px;
}

.count-num {
  color: #409eff;
  font-weight: bold;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 80px;
  color: #999;
  font-size: 16px;
}

.empty {
  text-align: center;
  padding: 80px;
  background: white;
  border-radius: 12px;
  color: #999;
}

.empty p {
  margin-bottom: 16px;
  font-size: 16px;
}

.manga-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.manga-card {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.manga-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.cover-wrapper {
  position: relative;
  overflow: hidden;
}

.manga-cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
  transition: transform 0.3s;
}

.manga-card:hover .manga-cover {
  transform: scale(1.05);
}

.cover-overlay {
  position: absolute;
  top: 8px;
  right: 8px;
}

.overlay-tag {
  background: rgba(0, 0, 0, 0.6);
  color: white;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
}

.manga-info {
  padding: 12px;
}

.manga-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #333;
}

.manga-author {
  font-size: 12px;
  color: #888;
  margin-bottom: 8px;
}

.manga-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 3px;
}

.meta-score {
  display: flex;
  align-items: center;
  gap: 3px;
  color: #f56c6c;
  font-weight: 600;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding: 20px 0;
}
</style>
