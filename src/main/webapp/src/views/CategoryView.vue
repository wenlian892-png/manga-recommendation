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
          @input="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
        <el-input
          v-model="searchAuthor"
          placeholder="搜索作者..."
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
          @input="handleSearch"
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

    <div v-if="loading">
      <PageSkeleton type="card-grid" :count="pagination.size" />
    </div>
    <div v-else>
      <div v-if="list.length === 0" class="empty">
        <EmptyState title="暂无数据" />
        <div class="empty-action">
          <el-button type="primary" plain @click="resetFilters">重置筛选</el-button>
        </div>
      </div>
      <div v-else class="manga-grid">
        <MangaCard
          v-for="item in list"
          :key="item.id"
          :manga="item"
          :show-category="true"
          :show-meta="true"
          cover-height="200px"
        />
      </div>
    </div>

    <div class="pagination-wrapper" v-if="pagination.total > 0">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="PAGE_SIZES"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'
import MangaCard from '@/components/MangaCard.vue'
import PageSkeleton from '@/components/PageSkeleton.vue'
import EmptyState from '@/components/EmptyState.vue'
import { MANGA_CATEGORIES, PAGE_SIZES } from '@/utils/constants'

const router = useRouter()
const route = useRoute()

const categoryOptions = MANGA_CATEGORIES.map(c => ({ label: c, value: c }))

const loading = ref(false)
const list = ref([])
const searchKeyword = ref('')
const searchAuthor = ref('')
const selectedCategory = ref('')

const pagination = reactive({
  current: 1,
  size: 24,
  total: 0
})

let searchTimer = null

async function doSearch() {
  pagination.current = 1
  fetchList()
}

function handleSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(doSearch, 300)
}

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
    if (searchAuthor.value.trim()) {
      params.author = searchAuthor.value.trim()
    }
    if (selectedCategory.value) {
      params.category = selectedCategory.value
    }
    const res = await request.get('/manga/page', { params })
    list.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (e) {
    ElMessage.error('获取漫画列表失败，请稍后重试')
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

function resetFilters() {
  searchKeyword.value = ''
  searchAuthor.value = ''
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

onMounted(() => {
  if (route.query.keyword) {
    searchKeyword.value = route.query.keyword
  }
  fetchList()
})
</script>

<style scoped>
.category-view {
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

.filters {
  display: flex;
  gap: var(--spacing-md);
}

.search-input {
  width: 280px;
}

.category-tabs {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  padding: var(--spacing-md) var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
  box-shadow: var(--shadow-sm);
}

.tab-scroll {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.tab-item {
  padding: 8px 18px;
  border-radius: 20px;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  background: var(--color-bg-secondary);
  cursor: pointer;
  transition: all var(--transition-normal);
  border: 1px solid transparent;
}

.tab-item:hover {
  background: var(--color-bg-secondary);
  color: var(--color-primary);
}

.tab-item.active {
  background: var(--color-primary);
  color: var(--color-bg-white);
  border-color: var(--color-primary);
}

.manga-count {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-md);
  padding: 0 4px;
}

.count-num {
  color: var(--color-primary);
  font-weight: var(--font-weight-bold);
}

.manga-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-xl);
}

.empty {
  text-align: center;
  padding: var(--spacing-xxl) 0;
}

.empty-action {
  margin-top: var(--spacing-md);
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding: var(--spacing-xl) 0;
}

@media (max-width: 1024px) {
  .manga-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .manga-grid {
    grid-template-columns: 1fr;
  }

  .filters {
    flex-direction: column;
  }

  .search-input {
    width: 100% !important;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }
}
</style>
