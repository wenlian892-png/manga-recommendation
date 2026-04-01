<template>
  <div class="manga-card" @click="goDetail">
    <div class="cover-wrapper">
      <img
        :src="manga.coverUrl || defaultCover"
        class="manga-cover"
        :style="{ height: coverHeight }"
        :alt="manga.title"
        loading="lazy"
        @error="handleImgError"
      />
      <span v-if="showCategory && manga.category" class="cover-tag">{{ manga.category }}</span>
    </div>
    <div class="manga-info">
      <h3 class="manga-title">{{ manga.title }}</h3>
      <p v-if="manga.author" class="manga-author">{{ manga.author }}</p>
      <div v-if="showMeta" class="manga-meta">
        <span v-if="manga.clickCount" class="meta-item">
          <el-icon :size="12"><View /></el-icon>
          {{ formatNumber(manga.clickCount) }}
        </span>
        <span v-if="manga.avgScore" class="meta-score">
          <el-icon :size="12"><Star /></el-icon>
          {{ manga.avgScore.toFixed(1) }}
        </span>
      </div>
      <slot name="extra" />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { View, Star } from '@element-plus/icons-vue'
import { DEFAULT_COVER } from '@/utils/constants'
import { formatNumber } from '@/utils/helpers'

const defaultCover = DEFAULT_COVER

const props = defineProps({
  manga: {
    type: Object,
    required: true
  },
  showCategory: {
    type: Boolean,
    default: false
  },
  showMeta: {
    type: Boolean,
    default: false
  },
  coverHeight: {
    type: String,
    default: '280px'
  }
})

const router = useRouter()

function goDetail() {
  if (props.manga && props.manga.id) {
    router.push({ name: 'MangaDetail', params: { id: props.manga.id } })
  }
}

function handleImgError(e) {
  e.target.src = defaultCover
}
</script>

<style scoped>
.manga-card {
  background: #fff;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: transform var(--transition-normal), box-shadow var(--transition-normal);
  overflow: hidden;
}

.manga-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.cover-wrapper {
  position: relative;
  overflow: hidden;
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.manga-cover {
  width: 100%;
  object-fit: cover;
  display: block;
  transition: transform var(--transition-slow);
}

.manga-card:hover .manga-cover {
  transform: scale(1.03);
}

.cover-tag {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
}

.manga-info {
  padding: var(--spacing-sm) var(--spacing-md) var(--spacing-md);
}

.manga-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin: 0 0 4px 0;
}

.manga-author {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin: 0 0 4px 0;
}

.manga-meta {
  display: flex;
  gap: var(--spacing-sm);
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 2px;
}

.meta-score {
  display: flex;
  align-items: center;
  gap: 2px;
  color: var(--color-warning);
  font-weight: var(--font-weight-medium);
}
</style>
