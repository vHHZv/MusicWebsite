<template>
  <el-drawer
      v-model="ctrl"
      direction="ltr"
      size="30%"
      :before-close="beforeClose"
  >
    <template #header>
      <span id="drawerHeader">{{ msg }}</span>
    </template>
    <el-scrollbar max-height="calc(100vh - 50px)">
      <div class="demo-drawer__content">
        <!-- 用于插入表单 -->
        <slot></slot>
      </div>
    </el-scrollbar>
  </el-drawer>
</template>

<script>
export default {
  name: "BgDrawer"
}
</script>

<script setup>
import {computed} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";

const emit = defineEmits(['closeDrawer'])

const props = defineProps({
  isOpen: Boolean,
  msg: String
})

// 控制抽屉开关
const ctrl = computed({
  get() {
    return props.isOpen;
  },
  set() {
    return props.isOpen;
  }
})

// 关闭抽屉，弹出消息提示框
function beforeClose() {
  ElMessageBox.confirm(
      '所做的修改将会丢失，是否确认关闭信息输入框？',
      '注意',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
        buttonSize: 'default',
      }
  )
      .then(() => {
        ElMessage({
          showClose: true,
          type: 'info',
          message: '已取消操作',
          center: true,
        });
        // 通知父组件取消操作，关闭抽屉
        emit('closeDrawer')
      })
      .catch(() => {
      })
}
</script>

<style scoped>
/*标题*/
#drawerHeader {
  color: black;
  font-size: 20px;
  font-weight: bolder;
  margin-left: 10px;
}
</style>