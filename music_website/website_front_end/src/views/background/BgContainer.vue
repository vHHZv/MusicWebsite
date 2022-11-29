<template>
  <div class="common-layout">
    <el-container>

      <!--头部导航栏-->
      <el-header id="header">
        <BgHeader></BgHeader>
      </el-header>
      <el-container>

        <!--边栏导航模块-->
        <el-aside id="aside">
          <BgAside></BgAside>
        </el-aside>

        <!--内容部分-->
        <el-main id="main">
          <el-scrollbar max-height="calc(100vh - 50px)" always>
            <!--页面内容路由-->
            <router-view></router-view>
          </el-scrollbar>
        </el-main>

      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: "BgContainer"
}
</script>

<script setup>

import BgHeader from "@/components/background/BgHeader";
import BgAside from "@/components/background/BgAside";
</script>

<style scoped>
#header {
  height: 50px;
  padding: 0;
}

#aside {
  width: 170px
}

#main {
  padding: 10px 0 10px 10px;
  height: calc(100vh - 50px);
  background-color: #E4E7ED;
}
</style>