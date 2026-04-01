import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue')
  },
  {
    path: '/manga',
    redirect: '/category'
  },
  {
    path: '/category',
    name: 'Category',
    component: () => import('@/views/CategoryView.vue')
  },
  {
    path: '/manga/:id',
    name: 'MangaDetail',
    component: () => import('@/views/MangaDetailView.vue')
  },
  {
    path: '/leaderboard',
    name: 'Leaderboard',
    component: () => import('@/views/LeaderboardView.vue')
  },
  {
    path: '/recommend',
    name: 'Recommend',
    component: () => import('@/views/RecommendView.vue')
  },
  {
    path: '/forum',
    name: 'Forum',
    component: () => import('@/views/ForumView.vue')
  },
  {
    path: '/forum/:id',
    name: 'ForumDetail',
    component: () => import('@/views/ForumDetailView.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/ProfileView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/DashboardView.vue')
      },
      {
        path: 'manga',
        name: 'AdminManga',
        component: () => import('@/views/admin/MangaManageView.vue')
      },
      {
        path: 'user',
        name: 'AdminUser',
        component: () => import('@/views/admin/UserManageView.vue')
      },
      {
        path: 'algorithm',
        name: 'AdminAlgorithm',
        component: () => import('@/views/admin/AlgorithmCenter.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('manga_token')

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      const userStore = useUserStore()
      userStore.showLoginDialog = true
      next({ name: 'Home' })
      return
    }
  }

  if (to.matched.some(record => record.meta.requiresAdmin)) {
    const userStore = useUserStore()
    if (!userStore.currentUser || userStore.currentUser.role !== 1) {
      next({ name: 'Home' })
      return
    }
  }

  next()
})

router.afterEach(() => {
  window.scrollTo(0, 0)
})

export default router
