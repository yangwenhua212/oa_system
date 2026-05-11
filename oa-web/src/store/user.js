import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const SESSION_DURATION = 12 * 60 * 60 * 1000 // 12小时

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const loginTime = ref(Number(localStorage.getItem('loginTime') || 0))

  const isLoggedIn = computed(() => !!token.value)

  /** 判断登录是否超过 12 小时 */
  const isSessionExpired = computed(() => {
    if (!loginTime.value) return true
    return Date.now() - loginTime.value > SESSION_DURATION
  })

  function setToken(newToken) {
    token.value = newToken
    loginTime.value = Date.now()
    localStorage.setItem('token', newToken)
    localStorage.setItem('loginTime', String(loginTime.value))
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    loginTime.value = 0
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('loginTime')
  }

  return {
    token,
    userInfo,
    loginTime,
    isLoggedIn,
    isSessionExpired,
    setToken,
    setUserInfo,
    logout
  }
})
