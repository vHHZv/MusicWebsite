<template>
  <header>
    <span id="bg-name">
      <button @click="$router.push({path:'/management/bgHome'})">MvM 快去吃饭</button>
    </span>
    <span id="bg-setting">
      <el-dropdown size="default">
        <span class="el-dropdown-link">
          管理员&nbsp;{{ manager }}
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item :icon="Position">访问前台</el-dropdown-item>
            <el-dropdown-item :icon="SwitchButton" @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </span>
  </header>
</template>

<script>
export default {
  name: "BgHeader"
}
</script>

<script setup>
import {onMounted, ref} from "vue";
import {
  SwitchButton,
  Position
} from '@element-plus/icons-vue'
import request from "@/utils/request";
import {ElMessage} from "element-plus";
import router from "@/router";

// 管理员姓名
const manager = ref(null);

onMounted(() => {
  getAdminName();
})

// 获取管理员姓名
function getAdminName() {
  request.get('/admin/getAdminName')
      .then(res => {
        if (res.code === '1') {
          manager.value = res.data
        }
      })
      .catch(error => {
        console.log(error)
        ElMessage({
          showClose: true,
          type: 'error',
          message: '服务器连接异常',
          center: true,
        });
      })
}

// 退出登录
function logout() {
  window.sessionStorage.clear()
  ElMessage({
    showClose: true,
    type: 'success',
    message: "已退出登录",
    center: true,
  });
  router.replace("/management/login")
}
</script>

<style scoped>
header {
  height: 50px;
  background-color: #303133;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

#bg-name {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

#bg-name > button {
  width: 100%;
  height: 100%;
  border: none;
  margin-left: 20px;
  background-color: #303133;
  font-size: 23px;
  color: #ffffff;
  font-weight: bolder;
}

#bg-name > button:hover {
  font-size: 24px;
  cursor: pointer;
}

#bg-setting {
  height: 100%;
  margin-right: 40px;
  display: flex;
  justify-content: right;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  color: #ffffff;
}
</style>