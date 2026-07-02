import { defineStore } from 'pinia'
import { getToken, setToken, removeToken, getUserInfo, setUserInfo, removeUserInfo } from '@/utils/token'
import { loginApi, logoutApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken() || '',
    userInfo: getUserInfo() || null,
  }),
  getters: {
    isLoggedIn: state => !!state.token,
  },
  actions: {
    async login(form) {
      const res = await loginApi(form)
      const { token, user } = res.data
      setToken(token)
      setUserInfo(user)
      this.token = token
      this.userInfo = user
    },
    async logout() {
      try { await logoutApi() } catch {}
      removeToken()
      removeUserInfo()
      this.token = ''
      this.userInfo = null
      window.location.href = '/login.html'
    },
  }
})
