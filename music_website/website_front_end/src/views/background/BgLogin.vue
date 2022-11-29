<template>
  <div id="card-container">
    <el-card shadow="always" :body-style="{padding: 0}" id="card">
      <p id="title">后台登录</p>
      <BgForm
          :form-format="formFormat"
          :form-data="formData"
          :size="'default'"
          @keyup.enter="submitLoginForm"
      >
        <el-button type="primary" @click="submitLoginForm" id="btn">登录</el-button>
      </BgForm>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "BgLogin"
}
</script>

<script setup>
import {reactive, ref} from "vue";
import BgForm from "@/components/background/BgForm";
import request from "@/utils/request";
import {ElMessage} from "element-plus";
import router from "@/router";

// 表单格式
const formFormat = reactive([
  {id: 'adminName', label: '用户名', type: 'input', maxLength: 10, style: {'width': 240 + 'px'}},
  {id: 'passwd', label: '密码', type: 'password', maxLength: 15}
])

// 初始表单内容为空
const formData = ref({adminName: '', passwd: ''})

// 提交表单方法
function submitLoginForm() {
  // 直接构造数据对象
  request.post('/admin/login', {
    adminName: formData.value.adminName,
    passwd: formData.value.passwd
  })
      .then(res => {
        if (res.code === '1') {
          ElMessage({
            showClose: true,
            type: 'success',
            message: res.msg,
            center: true,
          });
          // 记录管理员信息
          window.sessionStorage.setItem('admin', res.data)
          // 跳转到后台管理首页
          router.push('/management/bgHome')
        } else {
          ElMessage({
            showClose: true,
            type: 'error',
            message: res.msg,
            center: true,
          });
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
</script>

<style scoped>
/* card 容器 */
#card-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  background-color: #E4E7ED;
}

/* card 设置 */
#card {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 5px 50px 15px 50px;
}

/* 标题 */
#title {
  text-align: center;
  font-weight: bolder;
  font-size: 30px;
}

/* 提交按钮 */
#btn {
  margin-left: 25%;
}
</style>