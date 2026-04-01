<template>
  <div class="footer">
    <div class="footer-content">
      <div class="footer-info">
        <p class="footer-title">漫画推荐系统</p>
        <p class="footer-desc">基于 Item-CF 协同过滤算法的个性化漫画推荐平台</p>
      </div>
      <div class="footer-links">
        <div class="link-group">
          <p class="link-title">快捷链接</p>
          <router-link to="/" class="footer-link">首页</router-link>
          <router-link to="/category" class="footer-link">漫画分类</router-link>
          <router-link to="/leaderboard" class="footer-link">排行榜</router-link>
        </div>
        <div class="link-group">
          <p class="link-title">社区</p>
          <router-link to="/recommend" class="footer-link">个性化推荐</router-link>
          <router-link to="/forum" class="footer-link">交流论坛</router-link>
        </div>
      </div>
    </div>
    <div class="footer-bottom">
      <p>&copy; {{ currentYear }} 漫画推荐系统 - 基于 Item-CF 协同过滤算法</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped>
.footer {
  background: var(--color-bg-dark, #2c3e50);
  color: var(--color-text-white, #ecf0f1);
  margin-top: 60px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  display: flex;
  justify-content: space-between;
}

.footer-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
}

.footer-desc {
  font-size: 14px;
  color: var(--color-text-secondary-light, #bdc3c7);
}

.footer-links {
  display: flex;
  gap: 60px;
}

.link-group {
  display: flex;
  flex-direction: column;
}

.link-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
}

.footer-link {
  color: var(--color-text-secondary-light, #bdc3c7);
  text-decoration: none;
  font-size: 14px;
  margin-bottom: 8px;
  transition: color var(--transition-normal);
}

.footer-link:hover {
  color: var(--color-primary);
}

.footer-bottom {
  background: var(--color-bg-darker, #1a252f);
  padding: 16px 20px;
  text-align: center;
  font-size: 13px;
  color: var(--color-text-disabled, #7f8c8d);
}

@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    gap: 24px;
  }

  .footer-links {
    gap: 40px;
  }
}
</style>
