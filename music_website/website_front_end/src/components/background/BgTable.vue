<template>
  <!-- 表格 -->
  <div>
    <el-table
        ref="table"
        :data="data"
        stripe
        tooltip-effect="light"
        height="503"
        :header-cell-style="{ background:'#545c64', color:'#fff', fontSize: '14px' }"
        style="width: 100%"
        highlight-current-row
    >
      <!-- 循环载入数据 -->
      <template v-for="(value, key, index) in dataNames" :key="index">

        <!--加载图片-->
        <el-table-column
            v-if="key === 'image' && index < mainCols"
            :label="value"
            :resizable="false"
            min-width="50"
        >
          <template #default="scope">
            <el-image
                style="position:absolute; top: 0; bottom: 0; left: 0; right: 0; width: 75%;"
                :src="scope.row[key]"
                :preview-src-list="[scope.row[key]]"
                fit="contain"
                hide-on-click-modal
                preview-teleported
            />
          </template>
        </el-table-column>

        <!--加载音频-->
        <el-table-column
            v-else-if="key === 'audio' && index < mainCols"
            :label="value"
            :resizable="false"
            min-width="80"
        >
          <template #default="scope">
            <el-button @click="play(scope.row)">播放</el-button>
          </template>
        </el-table-column>

        <!-- 链接到音频详情 -->
        <el-table-column
            v-else-if="key === 'connect' && index < mainCols"
            :label="value"
            :resizable="false"
            min-width="100"
        >
          <template #default="scope">
            <el-button @click="connect(scope.row)">查看</el-button>
          </template>
        </el-table-column>

        <!-- 图片、音频、链接外的其它内容 -->
        <el-table-column
            v-else-if="key !== 'image' && key !== 'audio' && key !== 'connect' && index < mainCols"
            :resizable="false"
            show-overflow-tooltip
            min-width="120"
            :label="value"
            :prop="key"
        />

        <!-- 描述列表 -->
        <el-table-column v-if="index === mainCols" type="expand" width="1">
          <template #default="scope">
            <!-- cutData 传入所有要显示的行数据，行下标取出对象；cutDataNames 传入要显示的行名 -->
            <BgDescriptions :data="cutData[scope.$index]" :data-names="cutDataNames"></BgDescriptions>
          </template>
        </el-table-column>
      </template>

      <!-- 操作列 -->
      <template v-if="data.length">
        <el-table-column
            fixed="right"
            :resizable="false"
            label="操作"
            width="152"
        >
          <template #default="scope">
            <el-button-group>
              <template v-if="mainCols < Object.keys(dataNames).length">
                <el-button type="primary" text @click="expand(scope.row)">更多</el-button>
              </template>
              <el-button type="primary" text @click="updateItem(scope.row)">修改</el-button>
              <el-button type="danger" text @click="toggleState(scope.row)">
                <span v-if="scope.row.deleted === '注销'">恢复</span>
                <span v-else>删除</span>
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </template>

      <!-- 空数据展示 -->
      <template #empty>
        <el-empty description="查无数据"/>
      </template>
    </el-table>
  </div>

  <!-- 分页 -->
  <div>
    <template id="pagination">

      <div id="btn">
        <el-button type="primary" @click="location.reload()">刷新</el-button>
        <el-button type="primary" @click="$emit('add')">新增</el-button>
        <el-button type="primary">导入</el-button>
        <el-button type="primary">导出</el-button>
      </div>

      <div v-if="'audio' in dataNames">
        <el-button @mouseenter="showPlayer = true">播放控制</el-button>
        <div v-show="showPlayer">
          <el-dialog
              v-model="dialogVisible"
              align-center
              title="音乐播放器"
              center
              :show-close="false"
              :close-on-click-modal="false"
              :close-on-press-escape="false"
              @mouseleave="showPlayer = false"
          >
            <MusicPlayer ref="player" :fixed="false"></MusicPlayer>
          </el-dialog>
        </div>
      </div>

      <el-pagination
          v-model:currentPage="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          small
          background
          layout="prev, pager, next, sizes"
          :page-count="totalPage"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </template>
  </div>

  <el-dialog
      v-model="dialogTableVisible"
      :title="gridData.title"
      :align-center="true"
      width="200"
      :destroy-on-close="true"
  >
    <BgGridTable
        :grid-data="gridData"
        @removeCollection="removeCollection"
    >
    </BgGridTable>
  </el-dialog>
</template>

<script>
export default {
  name: "BgTable"
}
</script>

<script setup>
import {computed, getCurrentInstance, onUnmounted, ref} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import BgDescriptions from "@/components/background/BgDescriptions";
import MusicPlayer from "@/components/MusicPlayer";
import BgGridTable from "@/components/background/BgShowSongsDialog";

onUnmounted(() => {
  if ('audio' in dataNames.value) {
    player.value.pause()
  }
})

// 上下文属性
const {proxy} = getCurrentInstance();

const props = defineProps({
  tableData: {
    type: Object,
    default() {
      return {
        data: [],
        dataNames: {},
        totalPage: 1,
        currentPage: 1,
        pageSize: 10,
        mainCols: 5,
      }
    },
  },
  gridData: {
    type: Object,
    default() {
      return {
        data: [],
        dataNames: {},
        mainCols: 5,
        title: '链接信息'
      }
    }
  }
})

const emit = defineEmits(['sizeChange', 'currentChange', 'updateItem', 'toggleState', 'add', 'getGridData', 'removeCollection'])

// 表格数据
const data = computed(() => props.tableData.data)

// 表头名称
const dataNames = computed(() => props.tableData.dataNames)

// 主页显示列数
const mainCols = computed(() => props.tableData.mainCols)

// 链接信息
const gridData = computed(() => props.gridData)

// 播放器
const player = ref()

// 播放器显示控制
let showPlayer = ref(false)
const dialogVisible = ref(true)

// 播放函数
function play(info) {
  player.value.play({
    id: info.songId,
    name: info.songName,
    artist: info.singerName,
    url: info.audio,
    cover: info.image,
    lrc: info.lyrics
  })
}

// 单曲信息对话框开关控制
const dialogTableVisible = ref(false)

// 操作行 id
let operatedLineId = ref()

// 展示单曲信息
function connect(info) {
  dialogTableVisible.value = true
  if ('uid' in info) {
    operatedLineId.value = info.uid
    emit('getGridData', info.uid)
  } else {
    emit('getGridData', info.sid)
  }
}

// 截取 更多 中显示的项目名
const cutDataNames = computed(() => {
  let tmp = {};
  for (let i = mainCols.value; i < Object.keys(dataNames.value).length; i++) {
    tmp[Object.keys(dataNames.value)[i]] = dataNames.value[Object.keys(dataNames.value)[i]];
  }
  return tmp;
})

// 截取 更多 中显示的项目内容
const cutData = computed(() => {
  let tmp = [];
  for (let i in data.value) {
    let tep1 = {}
    for (let j in Object.keys(cutDataNames.value)) {
      tep1[Object.keys(cutDataNames.value)[j]] = data.value[i][Object.keys(cutDataNames.value)[j]]
    }
    tmp.push(tep1)
  }
  return tmp;
})

// 展开行的更多按钮
function expand(row) {
  proxy.$refs.table.toggleRowExpansion(row)
}

// 修改数据
function updateItem(rowInfo) {
  let send = {};
  for (let i in Object.keys(dataNames.value)) {
    send[Object.keys(rowInfo)[i]] = Object.values(rowInfo)[i];
  }
  emit('updateItem', send)
}

// 删除数据
function toggleState(rowInfo) {
  ElMessageBox.confirm(
      '确认继续操作?',
      'Warning',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
        buttonSize: 'default',
      }
  )
      .then(() => {
        // 通知父组件删除数据
        emit('toggleState', rowInfo)
      })
      .catch(() => {
        ElMessage({
          showClose: true,
          type: 'info',
          message: '已取消',
          center: true,
        });
      })
}

// 删除用户收藏
function removeCollection(songId) {
  emit('removeCollection', operatedLineId.value, songId)
}

// 所有页码数据的变化经由父组件传递给 computed 方法赋值
// 总页数
const totalPage = computed(() => props.tableData.totalPage)

//当前页
const currentPage = computed(() => props.tableData.currentPage)

// 每页显示数据量
const pageSize = computed(() => props.tableData.pageSize)

// 变更每页显示数据量
const handleSizeChange = (newPageSize) => {
  const current = Math.floor((currentPage.value - 1) * pageSize.value / newPageSize + 1)
  emit('sizeChange', current, newPageSize)
}

// 换页
const handleCurrentChange = (newCurrentPage) => {
  emit('currentChange', newCurrentPage, pageSize.value)
}
</script>

<style scoped>
#pagination {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-top: 2vh;
}

#btn {
  margin-left: 1.5vw;
}
</style>