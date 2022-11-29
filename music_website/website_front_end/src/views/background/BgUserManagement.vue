<template>
  <!-- 抽屉，插入表单组件，isOpen控制打开或关闭，msg为抽屉标题，监听 isSubmit 事件，布尔值表示是否提交 -->
  <BgDrawer :is-open="isOpenDrawer" :msg="drawerMsg" @closeDrawer="closeDrawer">
    <!-- 表单 -->
    <BgForm
        :form-format="drawerFormat"
        :form-data="drawerForm"
        :confirm-text="'提交'"
        :cancel-text="'重置'"
        ref="drawerFormRef"
        @submitForm="submitDrawerForm"
        @resetForm="resetDrawerForm"
    >
    </BgForm>
  </BgDrawer>

  <div id="main">
    <!-- 用户查询板块 -->
    <el-card id="search-area" class="box-card" shadow="hover">
      <!-- 表单 -->
      <el-row>
        <el-col :span="24">
          <BgForm
              :form-format="searchFormat"
              :form-data="searchForm"
              :confirm-text="'查询'"
              :cancel-text="'重置'"
              ref="searchFormRef"
              :is-inline="true"
              :label-width="80"
              @submitForm="submitSearchForm"
              @resetForm="resetSearchForm"
          >
          </BgForm>
        </el-col>
      </el-row>
    </el-card>

    <!-- 数据展示板块 -->
    <el-card class="box-card" shadow="hover" :body-style="{ padding: '0px', paddingBottom: '10px' }">
      <BgTable :table-data="tableData"
               :grid-data="gridData"
               @sizeChange="pageChange"
               @currentChange="pageChange"
               @add="openDrawerToAdd"
               @updateItem="openDrawerToUpdate"
               @toggleState="toggleState"
               @getGridData="getGridData"
               @removeCollection="removeCollection"
      >
      </BgTable>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "BgUserManagement"
}
</script>

<script setup>
import {onMounted, reactive, ref} from "vue";
import BgDrawer from "@/components/background/BgDrawer";
import BgTable from "@/components/background/BgTable";
import BgForm from "@/components/background/BgForm";
import request from "@/utils/request";
import {ElMessage} from "element-plus";

// 加载初始数据
onMounted(() => {
  getAllData(1, 10);
})

// 抽屉表单 ref
const drawerFormRef = ref()

// 检索表单 ref
const searchFormRef = ref()

// 抽屉开关
let isOpenDrawer = ref(false)

// 抽屉标题
let drawerMsg = ref('')

// 判断抽屉表单是否处于更新状态
let isUpdate = ref(false)

// 传入表格的数据
let tableData = reactive({
  data: [],
  dataNames: {},
  pageSize: 10,
  currentPage: 1,
  totalPage: 1,
  mainCols: 7,
})

const defaultTableData = reactive({
  image: '头像',
  uid: '用户编号',
  username: '姓名',
  connect: '收藏列表',
  passwd: '密码',
  gender: '性别',
  phoneNum: '手机号码',
  email: '电子邮箱',
  birthday: '生日',
  info: '个人简介',
  deleted: '状态',
})

// 用户收藏单曲
const gridData = reactive({
  data: [],
  dataNames: {
    image: '封面',
    audio: '音频',
    songId: '单曲编号',
    songName: '单曲名',
    singerName: '歌手名',
    info: '单曲简介',
    deleted: '状态',
    lyrics: '歌词',
  },
  mainCols: 7,
  title: '用户收藏列表'
})

// 检索表单结构
const searchFormat = reactive([
  {id: 'username', label: '用户名', type: 'input', maxLength: 10, style: {'width': 140 + 'px'}},
  {id: 'phoneNum', label: '手机号码', type: 'input', maxLength: 11, style: {'width': 140 + 'px'}},
  {id: 'email', label: '电子邮箱', type: 'input', maxLength: 30, style: {'width': 260 + 'px'}},
])

// 检索表单
let searchForm = ref({})

// 用于发送的检索表单
let searchFormForSend = ref({})

// 抽屉表单结构
const drawerFormat = reactive([
  {id: 'username', label: '用户名', type: 'input', maxLength: 10, style: {'width': 240 + 'px'}},
  {id: 'passwd', label: '密码', type: 'password', maxLength: 20, style: {'width': 240 + 'px'}},
  {id: 'gender', label: '性别', type: 'select', options: ['男', '女'], style: {'width': 100 + 'px'}},
  {id: 'phoneNum', label: '手机号码', type: 'input', maxLength: 11, style: {'width': 240 + 'px'}},
  {id: 'email', label: '电子邮箱', type: 'input', maxLength: 30, style: {'width': 240 + 'px'}},
  {id: 'birthday', label: '生日', type: 'date', style: {'width': 240 + 'px'}},
  {id: 'avatar', label: '头像', type: 'image', action: '/api/image/upload'},
  {id: 'info', label: '介绍', type: 'textarea', style: {'width': 240 + 'px'}},
])

// 抽屉表单
let drawerForm = ref({})

// 用于发送的抽屉表单
let drawerFormForSend = ref({})

// 点击添加用户按钮（表格新增键）
function openDrawerToAdd() {
  drawerMsg.value = '新增用户信息';
  isUpdate.value = false;
  isOpenDrawer.value = true;
}

// 点击表格数据修改
function openDrawerToUpdate(rowInfo) {
  drawerMsg.value = '修改用户信息';
  isUpdate.value = true;
  isOpenDrawer.value = true;
  drawerForm.value = rowInfo;
}

// 处理关闭抽屉事件
function closeDrawer() {
  // 刷新 upload 组件
  drawerFormRef.value.changeUploadRefresher()
  isOpenDrawer.value = false;
  drawerForm.value = [];
}

// 检索表单重置
function resetSearchForm() {
  searchForm.value = []
}

// 抽屉表单重置
function resetDrawerForm() {
  drawerForm.value = []
}

// 抽屉表单确认
function submitDrawerForm() {
  // 封装好待发送的抽屉表单供下面函数调用发送给后端
  setDrawerFormForSend()
  closeDrawer()
  if (isUpdate.value) {
    modifyData()
  } else {
    addData()
  }
}

// 封装待发送的检索表单
function setSearchFormForSend() {
  for (let i in searchFormat) {
    searchFormForSend.value[searchFormat[i].id] = searchForm.value[searchFormat[i].id] === undefined ? '' : searchForm.value[searchFormat[i].id]
  }
}

// 封装待发送的抽屉表单
function setDrawerFormForSend() {
  // 判断是否存在 uid 字段用于更新
  if ('uid' in drawerForm.value) {
    drawerFormForSend.value['uid'] = drawerForm.value['uid']
  }
  for (let i in drawerFormat) {
    if (drawerFormat[i].id === 'avatar') {
      drawerFormForSend.value['avatar'] = drawerForm.value['image'] === undefined ? '' : drawerForm.value['image']
      const regPos = /^[0-9]+$/;
      // 不是图片编号
      if (!regPos.test(drawerFormForSend.value['avatar'])) {
        drawerFormForSend.value['avatar'] = ''
      }
      continue
    }
    drawerFormForSend.value[drawerFormat[i].id] = drawerForm.value[drawerFormat[i].id] === undefined ? '' : drawerForm.value[drawerFormat[i].id]
  }
}

// 添加用户数据
function addData() {
  request.post('/user/addUser', drawerFormForSend.value)
      .then(res => {
        if (res.code === '1') {
          getAllData(tableData.currentPage, tableData.pageSize)
          ElMessage({
            showClose: true,
            type: 'success',
            message: res.msg,
            center: true,
          });
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
          message: '连接服务器异常',
          center: true,
        });
      })
}

// 修改用户数据，数据在 drawerForm 中，经过 drawerFormForSend 包装
function modifyData() {
  request.put('/user/modifyUser', drawerFormForSend.value)
      .then(res => {
        getAllData(tableData.currentPage, tableData.pageSize)
        if (res.code === '1') {
          ElMessage({
            showClose: true,
            type: 'success',
            message: res.msg,
            center: true,
          });
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
          message: '连接服务器异常',
          center: true,
        });
      })
}

// 切换用户状态
function toggleState(rowInfo) {
  let state
  if (rowInfo.deleted === '正常') {
    state = 0
  } else {
    state = 1
  }
  request.delete('/user/toggleState/' + rowInfo.uid + '/' + state)
      .then(res => {
        if (res.code === '1') {
          ElMessage({
            showClose: true,
            type: 'success',
            message: res.msg,
            center: true,
          });
          getAllData(tableData.currentPage, tableData.pageSize)
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
          message: '连接服务器异常',
          center: true,
        });
      })
}

// 删除用户收藏
function removeCollection(uid, songId) {
  request.delete('/user/clearCollectSong', {
    params: {
      user: uid,
      song: songId,
    }
  })
      .then(res => {
        if (res.code === '1') {
          getGridData(uid)
          ElMessage({
            showClose: true,
            type: 'success',
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
          message: '连接服务器异常',
          center: true,
        });
      })
}

// 查询用户数据，当前页、每页条数、数据
function selectData(pageSize, username, phoneNum, email) {
  request.get('/user/searchAll', {
    params: {
      currentPage: 1,
      pageSize: pageSize,
      username: username,
      phoneNum: phoneNum,
      email: email,
    }
  })
      .then(res => {
        if (res.code === '1') {
          console.log(res)
          tableData.currentPage = 1
          tableData.pageSize = pageSize
          tableData.totalPage = res.data.totalPage
          tableData.dataNames = defaultTableData
          tableData.data = res.data.data
        } else {
          tableData.dataNames = {}
          tableData.data = []
          ElMessage({
            showClose: true,
            type: 'error',
            message: res.msg,
            center: true,
          });
        }
      })
      .catch(error => {
        tableData.dataNames = {}
        tableData.data = []
        console.log(error)
        ElMessage({
          showClose: true,
          type: 'error',
          message: '连接服务器异常',
          center: true,
        });
      })
}

// 页码、每页显示条数变化，参数为要 显示的页码 和 每页显示的数据条数
function pageChange(newCurrentPage, newPageSize) {
  getAllData(newCurrentPage, newPageSize)
}

// 检索表单查询
function submitSearchForm() {
  setSearchFormForSend()
  selectData(tableData.pageSize, searchFormForSend.value.username, searchFormForSend.value.phoneNum, searchFormForSend.value.email)
}

// 获取单个歌手的所有单曲
function getGridData(uid) {
  request.get('/user/getOneUser/' + uid)
      .then(res => {
        if (res.code === '1' && res.data.songs !== null) {
          gridData.data = res.data.songs
        } else {
          gridData.data = []
        }
      })
      .catch(error => {
        gridData.data = []
        console.log(error)
        ElMessage({
          showClose: true,
          type: 'error',
          message: '连接服务器异常',
          center: true,
        });
      })
}

// 获取用户数据
function getAllData(currentPage, pageSize) {
  request.get('/user/all', {
    params: {
      currentPage: currentPage,
      pageSize: pageSize
    }
  })
      .then(res => {
        if (res.code === '1') {
          tableData.currentPage = res.data.currentPage
          tableData.pageSize = res.data.pageSize
          tableData.totalPage = res.data.totalPage
          tableData.dataNames = defaultTableData
          tableData.data = res.data.data
        } else {
          tableData.dataNames = {}
          tableData.data = []
          ElMessage({
            showClose: true,
            type: 'error',
            message: res.msg,
            center: true,
          });
        }
      })
      .catch(error => {
        tableData.dataNames = {}
        tableData.data = []
        console.log(error)
        ElMessage({
          showClose: true,
          type: 'error',
          message: '连接服务器异常',
          center: true,
        });
      })
}
</script>

<style scoped>
#main {
  margin-right: 10px;
}

#search-area {
  margin-bottom: 5px;
  height: 65px;
}
</style>