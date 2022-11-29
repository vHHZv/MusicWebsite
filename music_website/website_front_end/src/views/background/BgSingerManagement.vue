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
    <!-- 歌手查询板块 -->
    <el-card id="search-area" class="box-card" shadow="hover" style="padding-left: 1px">
      <!-- 表单 -->
      <el-row>
        <el-col :span="17">
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
      >
      </BgTable>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "BgSingerManagement"
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

// 表头
const defaultTableData = reactive({
  image: '头像',
  sid: '歌手编号',
  singerName: '姓名',
  alias: '歌手别名',
  info: '个人简介',
  connect: '单曲列表',
  deleted: '状态',
})

// 歌手单曲
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
  title: '歌手单曲列表'
})

// 检索表单结构
const searchFormat = reactive([
  {id: 'singerName', label: '歌手名', type: 'input', maxLength: 10, style: {'width': 140 + 'px'}},
  {id: 'alias', label: '歌手别名', type: 'input', maxLength: 20, style: {'width': 200 + 'px'}},
])

// 检索表单
let searchForm = ref({})

// 用于发送的检索表单
let searchFormForSend = ref({})

// 抽屉表单结构
const drawerFormat = reactive([
  {id: 'singerName', label: '歌手名', type: 'input', maxLength: 10, style: {'width': 240 + 'px'}},
  {id: 'alias', label: '歌手别名', type: 'input', maxLength: 20, style: {'width': 240 + 'px'}},
  {id: 'avatar', label: '头像', type: 'image', action: '/api/image/upload'},
  {id: 'info', label: '介绍', type: 'textarea', style: {'width': 240 + 'px'}},
])

// 抽屉表单
let drawerForm = ref({})

// 用于发送的抽屉表单
let drawerFormForSend = ref({})

// 点击添加用户按钮（表格新增键）
function openDrawerToAdd() {
  drawerMsg.value = '新增歌手信息';
  isUpdate.value = false;
  isOpenDrawer.value = true;
}

// 点击表格数据修改
function openDrawerToUpdate(rowInfo) {
  drawerMsg.value = '修改歌手信息';
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
  // 判断是否存在 sid 字段用于更新
  if ('sid' in drawerForm.value) {
    drawerFormForSend.value['sid'] = drawerForm.value['sid']
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

// 添加歌手数据
function addData() {
  request.post('/singer/addSinger', drawerFormForSend.value)
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

// 修改歌手数据，数据在 drawerForm 中，经过 drawerFormForSend 包装
function modifyData() {
  request.put('/singer/modifySinger', drawerFormForSend.value)
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

// 切换歌手状态
function toggleState(rowInfo) {
  let state
  if (rowInfo.deleted === '正常') {
    state = 0
  } else {
    state = 1
  }
  request.delete('/singer/toggleState/' + rowInfo.sid + '/' + state)
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

// 查询歌手数据，当前页、每页条数、数据
function selectData(pageSize, singerName, alias) {
  request.get('/singer/searchAll', {
    params: {
      currentPage: 1,
      pageSize: pageSize,
      singerName: singerName,
      alias: alias,
    }
  })
      .then(res => {
        if (res.code === '1') {
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
  selectData(tableData.pageSize, searchFormForSend.value.singerName, searchFormForSend.value.alias)
}

// 获取单个歌手的所有单曲
function getGridData(sid) {
  request.get('/singer/getOneSinger/' + sid)
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

// 获取歌手数据
function getAllData(currentPage, pageSize) {
  request.get('/singer/all', {
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