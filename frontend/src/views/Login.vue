<template>
  <div class="login-container">
    <div class="login-box">
      <h2>网上立案系统</h2>
      <el-form @submit.prevent="handleLogin">
        <el-form-item>
          <el-input v-model="username" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-button type="primary" @click="handleLogin" size="large" style="width:100%">
          登录
        </el-button>
      </el-form>
      <p class="tip">
        测试账号：admin / staff01 / reviewer01，密码 123456
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api'

const username = ref('staff01')
const password = ref('123456')
const router = useRouter()

async function handleLogin() {
  if (!username.value || !password.value) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  try {
    const res: any = await authApi.login(username.value, password.value)
    if (res.code === 200) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('role', res.data.role)
      localStorage.setItem('realName', res.data.realName)
      ElMessage.success('登录成功')
      router.push('/cases')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '登录失败')
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #1a4d8f 0%, #4a90d9 100%);
}
.login-box {
  width: 420px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.2);
}
h2 {
  text-align: center;
  color: #1a4d8f;
  margin-bottom: 30px;
  font-size: 24px;
}
.tip {
  color: #888;
  font-size: 12px;
  margin-top: 20px;
  text-align: center;
}
</style>