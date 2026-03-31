<template>
  <div class="manga-view">
    <div class="page-header">
      <h1>漫画分类</h1>
      <div class="filters">
        <el-input v-model="filters.title" placeholder="搜索标题" style="width: 200px;" @keyup.enter="fetchList" />
        <el-input v-model="filters.author" placeholder="搜索作者" style="width: 200px;" @keyup.enter="fetchList" />
        <el-select v-model="filters.category" placeholder="分类" clearable style="width: 150px;">
          <el-option v-for="cat in categoryOptions" :key="cat.value" :label="cat.label" :value="cat.value" />
        </el-select>
        <el-button type="primary" @click="fetchList">搜索</el-button>
      </div>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="list.length === 0" class="empty">暂无数据</div>
    <div v-else class="manga-grid">
          <MangaCard v-for="item in list" :key="item.id" :manga="item" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { mangaApi } from '@/api/manga'
import { ElMessage } from 'element-plus'
import MangaCard from '@/components/MangaCard.vue'

const route = useRoute()

const COMMON_CATEGORIES = ['热血', '恋爱', '冒险', '悬疑', '运动']

const loading = ref(false)
const list = ref([])
const categoryOptions = ref(COMMON_CATEGORIES.map(c => ({ label: c, value: c })))
const filters = reactive({
  title: '',
  author: '',
  category: ''
})
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

async function fetchList() {
  loading.value = true
  try {
    const res = await mangaApi.getMangaPage({
      current: pagination.current,
      size: pagination.size,
      title: filters.title || null,
      author: filters.author || null,
      category: filters.category || null
    })
    list.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (e) {
    console.error('获取漫画列表失败:', e)
    ElMessage.error('获取漫画列表失败，请稍后重试')
    list.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}



onMounted(() => {
  if (route.query.keyword) {
    filters.title = route.query.keyword
  }
  fetchList()
})
</script>

<style scoped>
.manga-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  margin-bottom: 16px;
}

.filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.loading, .empty {
  text-align: center;
  padding: 60px;
  color: #999;
}

.manga-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.manga-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.manga-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.manga-cover {
  width: 100%;
  height: 240px;
  object-fit: cover;
}

.manga-info {
  padding: 12px;
}

.manga-title {
  font-size: 16px;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.manga-author, .manga-category {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .manga-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .filters {
    flex-direction: column;
    gap: 8px;
  }
  
  .filters .el-input,
  .filters .el-select {
    width: 100% !important;
  }
  
  .filters .el-button {
    width: 100%;
  }
}
</style>
