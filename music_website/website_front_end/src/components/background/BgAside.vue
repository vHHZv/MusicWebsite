<template>
  <div>
    <el-scrollbar max-height="calc(100vh - 50px)">
      <el-row class="tac">
        <el-col>
          <el-menu
              active-text-color="#ffd04b"
              background-color="#545c64"
              class="el-menu-vertical-demo"
              :default-active="$route.path"
              text-color="#fff"
              router
          >
            <el-menu-item index="/management/bgHome">
              <el-icon>
                <House/>
              </el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-sub-menu index="2">
              <template #title>
                <el-icon>
                  <Headset/>
                </el-icon>
                <span>乐库管理</span>
              </template>
              <!--<el-menu-item index="/management/newSong">&thinsp;&thinsp;&thinsp;新曲速递</el-menu-item>-->
              <!--<el-menu-item index="/management/playlistClass">&thinsp;&thinsp;&thinsp;歌单分类</el-menu-item>-->
              <el-menu-item index="/management/bgSingerManagement">&thinsp;&thinsp;&thinsp;歌手管理</el-menu-item>
              <!--<el-menu-item index="/management/album">&thinsp;&thinsp;&thinsp;专辑歌单</el-menu-item>-->
              <el-menu-item index="/management/bgSongManagement">&thinsp;&thinsp;&thinsp;曲库管理</el-menu-item>
              <!--<el-menu-item index="/management/listSetting">&thinsp;&thinsp;&thinsp;榜单设置</el-menu-item>-->
            </el-sub-menu>
            <el-menu-item index="/management/bgUserManagement">
              <el-icon>
                <User/>
              </el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-sub-menu index="4">
              <template #title>
                <el-icon>
                  <setting/>
                </el-icon>
                <span>系统管理</span>
              </template>
              <el-menu-item index="4-1" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
              <el-menu-item index="4-2" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
              <el-menu-item index="4-3" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="5">
              <template #title>
                <el-icon>
                  <MostlyCloudy/>
                </el-icon>
                <span>在线开发</span>
              </template>
              <el-menu-item index="5-1" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
              <el-menu-item index="5-2" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
              <el-menu-item index="5-3" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="6">
              <template #title>
                <el-icon>
                  <Document/>
                </el-icon>
                <span>报表设计</span>
              </template>
              <el-menu-item index="6-1" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
              <el-menu-item index="6-2" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
              <el-menu-item index="6-3" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="7">
              <template #title>
                <el-icon>
                  <Monitor/>
                </el-icon>
                <span>系统监控</span>
              </template>
              <el-menu-item index="7-1" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
              <el-menu-item index="7-2" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
              <el-menu-item index="7-3" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="8">
              <template #title>
                <el-icon>
                  <Help/>
                </el-icon>
                <span>常见案例</span>
              </template>
              <el-menu-item index="4-1" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
              <el-menu-item index="4-2" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
              <el-menu-item index="4-3" disabled>&thinsp;&thinsp;&thinsp;待开发</el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-col>
      </el-row>
    </el-scrollbar>
  </div>
</template>

<script>
export default {
  name: "BgAside"
}
</script>

<script setup>
import {
  Document,
  House,
  Headset,
  Setting,
  User,
  MostlyCloudy,
  Monitor,
  Help,
} from '@element-plus/icons-vue'
</script>

<style scoped>
.el-menu-vertical-demo {
  width: 170px;
  min-height: calc(100vh - 50px);
}
</style>