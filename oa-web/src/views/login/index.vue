<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-box">
        <div class="login-header green">
          <h2>自动办公系统</h2>
        </div>

        <div class="login-body">
          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名/手机号"
                :prefix-icon="User"
                size="large"
                clearable
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                size="large"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="login-btn"
                @click="handleLogin"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="login-footer">
          <span>OA自动办公系统 v1.0</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login(loginForm)
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.userInfo)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #aac4bc 0%, #7cb0ca 50%, #eed5a9 100%);
}

.login-wrapper {
  position: relative;
}

.login-box {
  width: 400px;
  background: #eee;
  border-radius: 8px;
  box-shadow: 0 10px 30px -10px rgba(0, 0, 0, 0.5);
  overflow: hidden;
}

.login-header {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;

  h2 {
    color: white;
    font-size: 24px;
    font-weight: 400;
    letter-spacing: 2px;
  }
}

.login-body {
  padding: 30px 40px;
}

.login-form {
  :deep(.el-input__wrapper) {
    border-radius: 3px;
    box-shadow: none;
    border: 1px solid #ccc;

    &:focus-within {
      border-color: #00a65a;
    }
  }

  :deep(.el-input__inner) {
    height: 40px;
    font-size: 14px;
  }
}

.login-btn {
  width: 100%;
  height: 42px;
  font-size: 16px;
  letter-spacing: 5px;
  background-color: #00a65a !important;
  border: none;
  border-radius: 3px;
  box-shadow: 1px 5px 20px -5px rgba(0, 0, 0, 0.4);

  &:hover {
    background-color: #008d4c !important;
  }
}

.login-footer {
  text-align: center;
  padding: 15px;
  font-size: 12px;
  color: #999;
  background: #f5f5f5;
  border-top: 1px solid #ddd;
}
</style>
