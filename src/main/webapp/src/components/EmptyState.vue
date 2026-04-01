<template>
  <div class="empty-state">
    <div v-if="icon" class="empty-state-icon">{{ icon }}</div>
    <h3 class="empty-state-title">{{ title }}</h3>
    <p v-if="description" class="empty-state-desc">{{ description }}</p>
    <el-button v-if="actionText" type="primary" @click="$emit('action')">
      {{ actionText }}
    </el-button>
  </div>
</template>

<script setup>
defineProps({
  icon: {
    type: String,
    default: ''
  },
  title: {
    type: String,
    default: '暂无数据'
  },
  description: {
    type: String,
    default: ''
  },
  actionText: {
    type: String,
    default: ''
  }
})

defineEmits(['action'])
</script>

<style scoped>
.empty-state {
  padding: 64px 20px;
  text-align: center;
}

.empty-state-icon {
  font-size: 48px;
  color: var(--color-text-placeholder);
  margin-bottom: 16px;
}

.empty-state-title {
  font-size: 18px;
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
  margin: 0 0 8px 0;
}

.empty-state-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0 0 20px 0;
}
</style>
