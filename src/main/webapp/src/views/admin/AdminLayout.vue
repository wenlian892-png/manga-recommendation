<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="admin-sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-header">
        <el-icon v-if="!isCollapsed" :size="24"><Reading /></el-icon>
        <span v-if="!isCollapsed" class="sidebar-title">管理后台</span>
        <el-button v-if="!isMobile" class="collapse-btn" :icon="isCollapsed ? Expand : Fold" text @click="isCollapsed = !isCollapsed" />
      </div>
      <nav class="sidebar-nav">
        <router-link to="/admin" class="nav-item" :class="{ active: $route.path === '/admin' }">
          <el-icon :size="18"><DataAnalysis /></el-icon>
          <span v-if="!isCollapsed">数据大盘</span>
        </router-link>
        <router-link to="/admin/manga" class="nav-item" :class="{ active: $route.path.startsWith('/admin/manga') }">
          <el-icon :size="18"><Document /></el-icon>
          <span v-if="!isCollapsed">漫画管理</span>
        </router-link>
        <router-link to="/admin/user" class="nav-item" :class="{ active: $route.path.startsWith('/admin/user') }">
          <el-icon :size="18"><User /></el-icon>
          <span v-if="!isCollapsed">用户管理</span>
        </router-link>
        <router-link to="/admin/algorithm" class="nav-item" :class="{ active: $route.path.startsWith('/admin/algorithm') }">
          <el-icon :size="18"><Setting /></el-icon>
          <span v-if="!isCollapsed">算法中心</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <router-link to="/" class="back-link">
          <el-icon :size="18"><Back /></el-icon>
          <span v-if="!isCollapsed">返回前台</span>
        </router-link>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="admin-main" :class="{ 'sidebar-collapsed': isCollapsed && !isMobile }">
      <!-- 顶部栏 -->
      <header class="admin-topbar">
        <el-button v-if="isMobile" :icon="Menu" text @click="showDrawer = true" />
        <h2 class="page-title">{{ pageTitle }}</h2>
        <div class="topbar-right">
          <span class="admin-badge">管理员</span>
          <el-dropdown @command="handleCommand">
            <span class="user-trigger">
              <el-avatar :size="32" :icon="UserFilled" />
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="home">返回前台</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 页面内容 -->
      <main class="admin-content">
        <router-view />
      </main>
    </div>

    <!-- 移动端抽屉 -->
    <el-drawer v-model="showDrawer" direction="ltr" size="220px" :show-close="false">
      <div class="sidebar-header">
        <el-icon :size="24"><Reading /></el-icon>
        <span class="sidebar-title">管理后台</span>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/admin" class="nav-item" :class="{ active: $route.path === '/admin' }" @click="showDrawer = false">
          <el-icon :size="18"><DataAnalysis /></el-icon>
          <span>数据大盘</span>
        </router-link>
        <router-link to="/admin/manga" class="nav-item" :class="{ active: $route.path.startsWith('/admin/manga') }" @click="showDrawer = false">
          <el-icon :size="18"><Document /></el-icon>
          <span>漫画管理</span>
        </router-link>
        <router-link to="/admin/user" class="nav-item" :class="{ active: $route.path.startsWith('/admin/user') }" @click="showDrawer = false">
          <el-icon :size="18"><User /></el-icon>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/algorithm" class="nav-item" :class="{ active: $route.path.startsWith('/admin/algorithm') }" @click="showDrawer = false">
          <el-icon :size="18"><Setting /></el-icon>
          <span>算法中心</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <router-link to="/" class="back-link" @click="showDrawer = false">
          <el-icon :size="18"><Back /></el-icon>
          <span>返回前台</span>
        </router-link>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Reading, Expand, Fold, DataAnalysis, Document, User, Setting, Back, Menu, UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapsed = ref(false)
const isMobile = ref(false)
const showDrawer = ref(false)

const titleMap = {
  '/admin': '数据大盘',
  '/admin/manga': '漫画管理',
  '/admin/user': '用户管理',
  '/admin/algorithm': '算法中心'
}

const pageTitle = computed(() => {
  return titleMap[route.path] || '管理后台'
})

function handleCommand(command) {
  if (command === 'home') {
    router.push('/')
  } else if (command === 'logout') {
    userStore.logout()
    router.push('/')
  }
}

function checkMobile() {
  isMobile.value = window.innerWidth < 768
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.admin-sidebar {
  width: 220px;
  background: #1d2129;
  color: white;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: var(--z-header);
  display: flex;
  flex-direction: column;
  transition: width var(--transition-normal);
  overflow: hidden;
}

.admin-sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  min-height: 56px;
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

.collapse-btn {
  margin-left: auto;
  color: rgba(255, 255, 255, 0.65);
}

.collapse-btn:hover {
  color: white;
}

.sidebar-nav {
  flex: 1;
  padding: 12px 0;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.65);
  transition: all var(--transition-fast);
  border-radius: var(--radius-md);
  margin: 2px 8px;
  text-decoration: none;
  white-space: nowrap;
}

.nav-item:hover,
.nav-item.active {
  color: white;
  background: rgba(255, 255, 255, 0.1);
}

.nav-item.active {
  background: var(--color-primary);
}

.sidebar-footer {
  padding: 12px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.back-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.45);
  transition: all var(--transition-fast);
  border-radius: var(--radius-md);
  margin: 2px 8px;
  text-decoration: none;
  white-space: nowrap;
}

.back-link:hover {
  color: rgba(255, 255, 255, 0.85);
  background: rgba(255, 255, 255, 0.06);
}

.admin-main {
  margin-left: 220px;
  flex: 1;
  transition: margin-left var(--transition-normal);
  background: var(--color-bg-page);
}

.admin-main.sidebar-collapsed {
  margin-left: 64px;
}

.admin-topbar {
  height: 56px;
  background: white;
  border-bottom: 1px solid var(--color-border-light);
  display: flex;
  align-items: center;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: var(--z-header);
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
  flex: 1;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.admin-badge {
  font-size: 12px;
  color: var(--color-text-secondary);
  background: var(--color-bg-page);
  padding: 2px 10px;
  border-radius: 10px;
}

.user-trigger {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.admin-content {
  padding: 24px;
}

/* 响应式 */
@media (max-width: 768px) {
  .admin-sidebar {
    display: none;
  }

  .admin-main {
    margin-left: 0;
  }

  .admin-content {
    padding: 16px;
  }

  .page-title {
    font-size: 16px;
  }
}
</style>
