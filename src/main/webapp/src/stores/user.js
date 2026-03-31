import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const currentUser = ref(null)

  const isLoggedIn = computed(() => currentUser.value !== null)

  const isAdmin = computed(() => currentUser.value && currentUser.value.role === 1)

  function setUser(user) {
    currentUser.value = user
    if (user) {
      localStorage.setItem('manga_user', JSON.stringify(user))
    } else {
      localStorage.removeItem('manga_user')
    }
  }

  function loadUser() {
    const stored = localStorage.getItem('manga_user')
    if (stored) {
      try {
        currentUser.value = JSON.parse(stored)
      } catch (e) {
        currentUser.value = null
      }
    }
  }

  function logout() {
    setUser(null)
  }

  return {
    currentUser,
    isLoggedIn,
    isAdmin,
    setUser,
    loadUser,
    logout
  }
})
