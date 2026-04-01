<template>
  <button class="manga-card" @click="handleClick" type="button">
    <img :src="manga.coverUrl || '/placeholder.png'" :alt="manga.title || '漫画封面'" class="manga-cover" loading="lazy" width="200" height="240" />
    <div class="manga-info">
      <h3 class="manga-title">{{ manga.title }}</h3>
      <p class="manga-author">{{ manga.author }}</p>
      <p v-if="manga.category" class="manga-category">{{ manga.category }}</p>
      <p v-if="manga.tip" class="manga-tip">{{ manga.tip }}</p>
    </div>
  </button>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  manga: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click'])
const router = useRouter()

function handleClick() {
  if (emit('click')) {
    emit('click', props.manga)
  } else {
    router.push(`/manga/${props.manga.id}`)
  }
}
</script>

<style scoped>
.manga-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  border: none;
  padding: 0;
  text-align: left;
  width: 100%;
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

.manga-tip {
  font-size: 12px;
  color: #e6a23c;
  margin-top: 4px;
  font-weight: 500;
}
</style>
