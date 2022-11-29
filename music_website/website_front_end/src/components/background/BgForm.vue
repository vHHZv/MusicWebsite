<template>
  <div>
    <el-form
        :model="form"
        :label-width="labelWidth"
        status-icon
        :inline="isInline"
        :size="size"
        scroll-to-error
    >
      <el-form-item v-for="(item, index) in formFormat" :key="index" :label="item.label">
        <el-input
            v-if="item.type === 'input'"
            type="input"
            :placeholder="'请输入' + item.label"
            v-model="form[item.id]"
            clearable
            :maxlength="item.maxLength"
            show-word-limit
            :style="item.style"
        >
        </el-input>
        <el-input
            v-if="item.type === 'password'"
            type="password"
            :placeholder="'请输入' + item.label"
            v-model="form[item.id]"
            clearable
            :maxlength="item.maxLength"
            show-password
            :style="item.style"
        >
        </el-input>
        <el-select
            v-else-if="item.type === 'select'"
            :placeholder="'请选择' + item.label"
            v-model="form[item.id]"
            clearable
            :style="item.style"
        >
          <el-option
              v-for="option in item.options" :key="option"
              :label="option"
              :value="option"
          >
          </el-option>
        </el-select>
        <el-select
            v-else-if="item.type === 'selectMultiple'"
            v-model="form[item.id]"
            multiple
            :placeholder="'请输入' + item.label"
            :style="item.style"
        >
          <el-option
              v-for="option in item.options" :key="option"
              :label="option"
              :value="option"
          />
        </el-select>
        <el-date-picker
            v-else-if="item.type === 'date'"
            type="date"
            :placeholder="'请输入' + item.label"
            v-model="form[item.id]"
            format="YYYY/MM/DD"
            value-format="YYYY-MM-DD"
            :style="item.style"
        >
        </el-date-picker>
        <el-date-picker
            v-else-if="item.type === 'daterange'"
            type="daterange"
            start-placeholder="请选择开始日期"
            end-placeholder="请选择结束日期"
            v-model="form[item.id]"
            format="YYYY/MM/DD"
            value-format="YYYY-MM-DD"
            :style="item.style"
        >
        </el-date-picker>
        <el-input
            v-else-if="item.type === 'textarea'"
            type="textarea"
            :placeholder="'请输入' + item.label"
            v-model="form[item.id]"
            :autosize="{ minRows: 4}"
            :style="item.style"
        >
        </el-input>
        <el-upload
            v-else-if="item.type === 'image'"
            :action="item.action"
            :limit="1"
            :key="uploadRefresher"
            :on-success="putPicIntoForm"
            :on-remove="removePicFromForm"
            :on-exceed="outOfRange"
        >
          <template #trigger>
            <el-button type="primary">
              <el-icon>
                <Upload/>
              </el-icon>&nbsp;选择文件  <!-- 直接将路径存至表单 -->
            </el-button>
          </template>
        </el-upload>
        <el-upload
            v-else-if="item.type === 'audio'"
            :action="item.action"
            :limit="1"
            :key="uploadRefresher"
            :on-success="putAudioIntoForm"
            :on-remove="removeAudioFromForm"
            :on-exceed="outOfRange"
        >
          <template #trigger>
            <el-button type="primary">
              <el-icon>
                <Upload/>
              </el-icon>&nbsp;选择文件  <!-- 直接将路径存至表单 -->
            </el-button>
          </template>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <slot></slot>
      </el-form-item>
      <el-form-item v-if="confirmText !== ''">
        <el-button type="primary" @click="onSubmit">{{ confirmText }}</el-button>
        <el-button @click="onReset">{{ cancelText }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: "BgForm"
}
</script>

<script setup>
import {computed, ref} from "vue";
import {Upload} from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";

defineExpose({changeUploadRefresher})

const emit = defineEmits(['submitForm', 'resetForm'])

const props = defineProps({
  //(id)dbname label type attrs(input: maxLength, select: options[], radio: radios[], selectMultiple
  //                            image: action[后端上传控制器])
  formFormat: {
    type: Array,
    default() {
      return []
    }
  },
  formData: {
    type: Object,
    default() {
      return {}
    }
  },
  confirmText: {
    type: String,
    default: ''
  },
  cancelText: {
    type: String,
    default: ''
  },
  isInline: {
    type: Boolean,
    default: false
  },
  size: {
    type: String,
    default: 'small'
  },
  rules: {
    type: Array,
    default() {
      return []
    }
  },
  labelWidth: {
    default: 'auto'
  }
})

// 表单
const form = computed(() => props.formData)

// 确认方法
function onSubmit() {
  uploadRefresher.value = new Date()
  emit('submitForm')
}

// 重置方法
function onReset() {
  uploadRefresher.value = new Date()
  removePicFromForm()
  emit('resetForm')
}

// 文件上传超出限制
function outOfRange() {
  ElMessage({
    showClose: true,
    type: 'error',
    message: '仅允许上传一份文件',
    center: true,
  });
}

// 将图片 id 放入表单
function putPicIntoForm(res) {
  form.value['image'] = res.data
  console.log(form.value['image'])
}

// 将图片 id 从表单移除
function removePicFromForm() {
  form.value['image'] = ''
}

// 将音频 id 放入表单
function putAudioIntoForm(res) {
  form.value['mp3'] = res.data
}

// 将音频 id 从表单移除
function removeAudioFromForm() {
  form.value['mp3'] = ''
}

// 上传组件刷新器
let uploadRefresher = ref(new Date())

// 供父组件调用刷新器的方法
function changeUploadRefresher() {
  uploadRefresher.value = new Date()
}
</script>

<style scoped>

</style>