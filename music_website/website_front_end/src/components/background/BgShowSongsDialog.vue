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

        <!-- 图片、音频的其它内容 -->
        <el-table-column
            v-else-if="key !== 'image' && key !== 'audio' && index < mainCols"
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
            :width="(gridData.title.indexOf('用户') !== -1) ? 110 : 65"
        >
          <template #default="scope">
            <el-button-group>
              <template v-if="mainCols < Object.keys(dataNames).length">
                <el-button type="primary" text @click="expand(scope.row)">更多</el-button>
              </template>
              <template v-if="gridData.title.indexOf('用户') !== -1">
                <el-button type="danger" text @click="removeCollection(scope.row)">
                  <span v-if="scope.row.deleted === '注销'">恢复</span>
                  <span v-else>删除</span>
                </el-button>
              </template>
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
  <!-- 下部 -->
  <div>
    <template id="pagination">
      <div v-if="'audio' in dataNames">
        <el-button @mouseenter="showPlayer = true">播放控制</el-button>
        <div v-show="showPlayer">
          <el-dialog
              v-model="dialogVisible"
              align-center
              title="音乐播放器"
              center
              :show-close="false"
              :close-on-press-escape="false"
              :close-on-click-modal="false"
              @mouseleave="showPlayer = false"
          >
            <MusicPlayer ref="player" :fixed="false"></MusicPlayer>
          </el-dialog>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
export default {
  name: "BgGridTable"
}
</script>

<script setup>
import BgDescriptions from "@/components/background/BgDescriptions";
import MusicPlayer from "@/components/MusicPlayer";
import {computed, getCurrentInstance, onUnmounted, ref} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";

const emit = defineEmits(['removeCollection'])

onUnmounted(() => {
  if ('audio' in dataNames.value) {
    player.value.pause()
  }
})

// 上下文属性
const {proxy} = getCurrentInstance();

const props = defineProps({
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

// 表格数据
const data = computed(() => props.gridData.data)

// 表头名称
const dataNames = computed(() => props.gridData.dataNames)

// 主页显示列数
const mainCols = computed(() => props.gridData.mainCols)

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

// 删除数据
function removeCollection(rowInfo) {
  ElMessageBox.confirm(
      '将删除用户收藏，是否确认继续操作?',
      'Warning',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
        buttonSize: 'default',
      }
  )
      .then(() => {
        emit('removeCollection', rowInfo.songId)
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
</script>

<style scoped>
#pagination {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-top: 2vh;
}
</style>