<template>
  <el-descriptions
      class="margin-top"
      :column="2"
      size="small"
      border
  >
    <template v-for="(value, key) in names" :key="key">
      <el-descriptions-item align="center">
        <template #label>
          <div class="cell-item">{{ value }}</div>
        </template>
        <el-image
            v-if="key === 'image'"
            style="position:absolute; top: 0; bottom: 0; left: 0; right: 0; width: 50%;"
            :src="dData[key]"
            :preview-src-list="[dData[key]]"
            fit="cover"
            hide-on-click-modal
            preview-teleported
        />
        <template v-else-if="key === 'deleted' && dData[key] === '1'">注销</template>
        <template v-else-if="key === 'deleted' && dData[key] === '0'">正常</template>
        <template v-else>{{ dData[key] }}</template>
      </el-descriptions-item>
    </template>
  </el-descriptions>
</template>

<script>
export default {
  name: "BgDescriptions"
}
</script>

<script setup>
import {computed} from "vue";

const props = defineProps({
  data: {
    type: Object,
    default() {
      return {};
    }
  },
  dataNames: {
    type: Object,
    default() {
      return {};
    }
  }
})

// 显示的数据内容
const dData = computed(() => props.data)

// 显示的数据名
const names = computed(() => props.dataNames)

</script>

<style scoped>

</style>