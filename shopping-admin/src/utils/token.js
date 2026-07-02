const TOKEN_KEY = 'admin_token'
const USER_KEY = 'admin_user'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getUserInfo() {
  try { return JSON.parse(localStorage.getItem(USER_KEY)) } catch { return null }
}

export function setUserInfo(info) {
  localStorage.setItem(USER_KEY, JSON.stringify(info))
}

export function removeUserInfo() {
  localStorage.removeItem(USER_KEY)
}
