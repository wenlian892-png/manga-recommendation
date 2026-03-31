<template>
  <div class="admin-layout">
    <div class="admin-sidebar">
      <h2>管理后台</h2>
      <nav>
        <router-link to="/admin" class="sidebar-item" :class="{ active: route.name === 'AdminDashboard' }">
          📊 数据大盘
        </router-link>
        <router-link to="/admin/manga" class="sidebar-item" :class="{ active: route.name === 'AdminManga' }">
          📚 漫画管理
        </router-link>
        <router-link to="/admin/user" class="sidebar-item" :class="{ active: route.name === 'AdminUser' }">
          👥 用户管理
        </router-link>
        <router-link to="/admin/algorithm" class="sidebar-item" :class="{ active: route.name === 'AdminAlgorithm' }">
          ⚙️ 算法控制
        </router-link>
        <router-link to="/" class="sidebar-item">
          ← 返回前台
        </router-link>
      </nav>
    </div>
    <div class="admin-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: calc(100vh - 60px);
}

.admin-sidebar {
  width: 220px;
  background: #2c3e50;
  color: white;
  padding: 20px 0;
}

.admin-sidebar h2 {
  padding: 0 20px;
  margin-bottom: 20px;
  font-size: 18px;
}

.sidebar-item {
  display: block;
  padding: 14px 20px;
  color: #ecf0f1;
  text-decoration: none;
  transition: background 0.3s;
}

.sidebar-item:hover {
  background: #34495e;
}

.sidebar-item.active {
  background: #409eff;
}

.admin-content {
  flex: 1;
  padding: 24px;
  background: #f5f5f5;
}
</style>
