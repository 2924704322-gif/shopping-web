import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import LoginView from './views/login/LoginView.vue'

const app = createApp(LoginView)
app.use(createPinia())
app.use(ElementPlus)
app.mount('#app')
