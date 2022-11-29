<template>
  <el-card id="card"  shadow="always" :body-style="{ padding: '0px' }" style="border-color: gray">
    <div id="image">
      <el-row :gutter="24">
        <el-col :span="12" :offset="12">
          <div id="image-right-box"></div>
        </el-col>
      </el-row>
    </div>
    <div class="bottom">
      <div class="links">
        <ul>
          <li><a href="https://cn.vuejs.org/guide/introduction.html" target="_blank" rel="noopener">Vue</a></li>
          <li><a href="https://element-plus.gitee.io/zh-CN/component/button.html" target="_blank"
                 rel="noopener">Element</a></li>
          <li><a href="https://baomidou.com/pages/24112f/" target="_blank" rel="noopener">Mybatis-Plus</a></li>
          <li><a href="https://music.163.com/" target="_blank" rel="noopener">网易云音乐</a></li>
          <li><a href="https://hutool.cn/docs/#/" target="_blank" rel="noopener">Hutool</a></li>
          <li><a href="https://aplayer.js.org/#/zh-Hans/" target="_blank" rel="noopener">APlayer</a></li>
        </ul>
      </div>
      <time class="time">{{ currentDate }}</time>
    </div>
  </el-card>
</template>

<script>
export default {
  name: "BgHome"
}
</script>

<script setup>
import {onMounted, ref} from "vue";

onMounted(() => {
  setInterval(() => currentDate.value = new Date().toLocaleString(), 1000);
})

let currentDate = ref(new Date().toLocaleString())
</script>

<style scoped>
#card {
  margin-right: 10px;
}

.time {
  font-size: 20px;
  color: black;
}

#image {
  height: 82vh;
  background-image: url("~@/assets/fox.jpg");
  background-size: cover;
}

#image-right-box {
  height: 70vh;
  margin: 40px 50px 0 10px;
  border-radius: 25px;
  /*background-color: rgba(0, 0, 0, 0.3);*/
  display: flex;
  align-items: center;
  justify-content: center;
}

.bottom {
  padding: 5px 15px 5px 10px;
  line-height: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: rgb(209, 219, 244);
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: gray;
  text-decoration: none;
  /*color: #42b983;*/
}

a:hover {
  color: #2c3e50;
  text-decoration: underline;
}

.links {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
}
</style>