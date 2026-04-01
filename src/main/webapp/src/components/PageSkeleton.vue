<template>
  <div class="page-skeleton">
    <!-- 卡片网格骨架 -->
    <div v-if="type === 'card-grid'" class="skeleton-card-grid">
      <div v-for="i in count" :key="i" class="skeleton-card">
        <div class="skeleton-shape skeleton-cover"></div>
        <div class="skeleton-card-info">
          <div class="skeleton-shape skeleton-text"></div>
          <div class="skeleton-shape skeleton-text-short"></div>
        </div>
      </div>
    </div>

    <!-- 列表骨架 -->
    <div v-else-if="type === 'list'" class="skeleton-list">
      <div v-for="i in count" :key="i" class="skeleton-list-item">
        <div class="skeleton-shape skeleton-list-long"></div>
        <div class="skeleton-shape skeleton-list-short"></div>
      </div>
    </div>

    <!-- 详情骨架 -->
    <div v-else-if="type === 'detail'" class="skeleton-detail">
      <div class="skeleton-shape skeleton-detail-cover"></div>
      <div class="skeleton-detail-info">
        <div class="skeleton-shape skeleton-detail-title"></div>
        <div class="skeleton-shape skeleton-detail-text"></div>
        <div class="skeleton-shape skeleton-detail-text"></div>
        <div class="skeleton-shape skeleton-detail-text-short"></div>
        <div class="skeleton-shape skeleton-detail-text"></div>
        <div class="skeleton-shape skeleton-detail-text-short"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    default: 'card-grid'
  },
  count: {
    type: Number,
    default: 6
  }
})
</script>

<style scoped>
@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.skeleton-shape {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: var(--radius-sm);
}

/* 卡片网格骨架 */
.skeleton-card-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-md);
}

.skeleton-card {
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: #fff;
}

.skeleton-cover {
  height: 200px;
  border-radius: 0;
}

.skeleton-card-info {
  padding: var(--spacing-sm) var(--spacing-md) var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.skeleton-text {
  height: 16px;
  width: 80%;
}

.skeleton-text-short {
  height: 14px;
  width: 50%;
}

/* 列表骨架 */
.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.skeleton-list-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: var(--spacing-md);
  background: #fff;
  border-radius: var(--radius-lg);
}

.skeleton-list-long {
  height: 16px;
  width: 70%;
}

.skeleton-list-short {
  height: 14px;
  width: 40%;
}

/* 详情骨架 */
.skeleton-detail {
  display: flex;
  gap: var(--spacing-lg);
  background: #fff;
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
}

.skeleton-detail-cover {
  width: 300px;
  min-width: 300px;
  height: 400px;
  flex-shrink: 0;
}

.skeleton-detail-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 8px;
}

.skeleton-detail-title {
  height: 24px;
  width: 50%;
}

.skeleton-detail-text {
  height: 16px;
  width: 90%;
}

.skeleton-detail-text-short {
  height: 16px;
  width: 60%;
}

/* 响应式 */
@media (max-width: 1024px) {
  .skeleton-card-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .skeleton-card-grid {
    grid-template-columns: 1fr;
  }

  .skeleton-detail {
    flex-direction: column;
  }

  .skeleton-detail-cover {
    width: 100%;
    min-width: unset;
    height: 280px;
  }
}
</style>
