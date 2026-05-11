import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// Request Interceptor
service.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Response Interceptor
service.interceptors.response.use(
  response => {
    const res = response.data
    
    if (res.code !== 200) {
      ElMessage.error(res.message || 'Request Failed')
      
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || 'Request Failed'))
    }
    
    return res
  },
  error => {
    ElMessage.error(error.message || 'Network Error')
    return Promise.reject(error)
  }
)

export default service
