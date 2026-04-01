<template>
  <div class="header" :class="{ scrolled: isScrolled }">
    <div class="header-content">
      <div class="header-left">
        <router-link to="/" class="logo">
          <el-icon :size="24" style="color: var(--color-primary)"><Reading /></el-icon>
          <span class="logo-text">漫推荐</span>
        </router-link>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索漫画..."
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>

      <div class="header-nav" v-show="!isMobile">
        <router-link to="/" class="nav-item">首页</router-link>
        <router-link to="/category" class="nav-item">分类</router-link>
        <router-link to="/leaderboard" class="nav-item">排行榜</router-link>
        <router-link to="/recommend" class="nav-item">推荐</router-link>
        <router-link to="/forum" class="nav-item">论坛</router-link>
        <router-link v-if="userStore.isAdmin" to="/admin" class="nav-item admin-link">
          管理后台
        </router-link>
      </div>

      <div class="header-right">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown @command="handleUserCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">{{ userStore.currentUser?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="showLoginDialog = true">登录</el-button>
        </template>
        <el-button v-if="isMobile" class="menu-btn" :icon="Menu" @click="showDrawer = true" />
      </div>
    </div>

    <el-drawer v-model="showDrawer" direction="ltr" size="200px" :show-close="false">
      <template #header>
        <span style="font-weight: bold; font-size: 18px;">导航菜单</span>
      </template>
      <div class="drawer-nav">
        <router-link to="/" class="drawer-nav-item" @click="showDrawer = false">首页</router-link>
        <router-link to="/category" class="drawer-nav-item" @click="showDrawer = false">分类</router-link>
        <router-link to="/leaderboard" class="drawer-nav-item" @click="showDrawer = false">排行榜</router-link>
        <router-link to="/recommend" class="drawer-nav-item" @click="showDrawer = false">推荐</router-link>
        <router-link to="/forum" class="drawer-nav-item" @click="showDrawer = false">论坛</router-link>
        <router-link v-if="userStore.isAdmin" to="/admin" class="drawer-nav-item" @click="showDrawer = false">管理后台</router-link>
      </div>
    </el-drawer>

    <el-dialog v-model="showLoginDialog" title="登录" width="400px" @close="resetLoginForm">
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="60px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" @keyup.enter="handleLogin" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showLoginDialog = false">取消</el-button>
        <el-button type="primary" @click="handleLogin">登录</el-button>
        <el-button text @click="showRegisterDialog = true; showLoginDialog = false">没有账号？去注册</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showRegisterDialog" title="注册" width="400px" @close="resetRegisterForm">
      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="60px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" @keyup.enter="handleRegister" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRegisterDialog = false">取消</el-button>
        <el-button type="primary" @click="handleRegister">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, UserFilled, Menu, Reading } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const searchKeyword = ref('')
const showLoginDialog = ref(false)
const showRegisterDialog = ref(false)
const showDrawer = ref(false)
const isMobile = ref(false)
const isScrolled = ref(false)

// 监听 store 中的 showLoginDialog 状态，支持跨组件触发登录弹窗
watch(() => userStore.showLoginDialog, (val) => {
  if (val) {
    showLoginDialog.value = true
    userStore.showLoginDialog = false
  }
})

const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
}

function handleScroll() {
  isScrolled.value = window.scrollY > 10
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
  window.removeEventListener('scroll', handleScroll)
})

const loginFormRef = ref(null)
const registerFormRef = ref(null)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度2-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

function resetLoginForm() {
  loginForm.username = ''
  loginForm.password = ''
}

function resetRegisterForm() {
  registerForm.username = ''
  registerForm.password = ''
  registerForm.confirmPassword = ''
}

function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ name: 'Category', query: { keyword: searchKeyword.value } })
  }
}

function handleUserCommand(command) {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
  } else if (command === 'profile') {
    router.push({ name: 'Profile' })
  }
}

async function handleLogin() {
  if (!loginFormRef.value) return
  try {
    await loginFormRef.value.validate()
  } catch {
    return
  }
  try {
    const res = await request.post('/user/login', loginForm)
    userStore.setUser(res.data)
    ElMessage.success('登录成功')
    showLoginDialog.value = false
    resetLoginForm()
    const redirect = route.query.redirect
    if (redirect) {
      router.push(redirect)
    } else {
      router.push({ name: 'Home' })
    }
  } catch (e) {
  }
}

async function handleRegister() {
  if (!registerFormRef.value) return
  try {
    await registerFormRef.value.validate()
  } catch {
    return
  }
  try {
    const res = await request.post('/user/register', {
      username: registerForm.username,
      password: registerForm.password
    })
    userStore.setUser(res.data)
    ElMessage.success('注册成功')
    showRegisterDialog.value = false
    resetRegisterForm()
  } catch (e) {
  }
}
</script>

<style scoped>
.header {
  background: var(--color-bg-white, #fff);
  position: sticky;
  top: 0;
  z-index: 1000;
  transition: box-shadow var(--transition-normal);
}

.header.scrolled {
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
  flex: 1;
}

.logo {
  text-decoration: none;
  margin-right: 20px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: var(--color-primary);
}

.search-input {
  width: 260px;
}

.header-nav {
  display: flex;
  align-items: center;
  margin: 0 40px;
}

.nav-item {
  position: relative;
  padding: 0 16px;
  color: var(--color-text-primary);
  text-decoration: none;
  font-size: 15px;
  transition: color var(--transition-normal);
  white-space: nowrap;
}

.nav-item::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 50%;
  width: 0;
  height: 2px;
  background: var(--color-primary);
  transition: all var(--transition-normal);
  transform: translateX(-50%);
}

.nav-item:hover {
  color: var(--color-primary);
}

.nav-item:hover::after,
.nav-item.router-link-active::after {
  width: 60%;
}

.nav-item.router-link-active {
  color: var(--color-primary);
}

.admin-link {
  color: var(--color-danger);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: var(--color-text-primary);
}

.menu-btn {
  display: none;
}

.drawer-nav {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.drawer-nav-item {
  padding: 14px 20px;
  color: var(--color-text-primary);
  text-decoration: none;
  font-size: 15px;
  border-bottom: 1px solid var(--color-border-light);
  transition: background var(--transition-normal);
}

.drawer-nav-item:hover {
  background: var(--color-bg-page, #f5f7fa);
  color: var(--color-primary);
}

@media (max-width: 768px) {
  .header-nav {
    display: none;
  }

  .search-input {
    width: 160px;
  }

  .menu-btn {
    display: inline-flex;
  }

  .username {
    display: none;
  }
}
</style>
