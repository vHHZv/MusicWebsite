import {createRouter, createWebHistory} from 'vue-router'
import request from "@/utils/request";
import {ElMessage} from "element-plus";

const routes = [
    // 后台默认页面
    {
        path: '/',
        redirect: '/management/login'
    },

    // 后台登录页面
    {
        path: '/management/login',
        name: 'bgLogin',
        meta: {title: '后台管理 登录'},
        component: () => import('../views/background/BgLogin')
    },

    // 导入后台管理页面容器，children配置嵌套路由
    {
        path: '/management',
        redirect: '/management/login',
        name: 'bgContainer',
        component: () => import('../views/background/BgContainer'),
        children: [
            {
                path: '/management/bgHome',
                name: 'bgHome',
                meta: {bgEnterCheck: true, title: '后台管理 首页'},
                component: () => import('../views/background/BgHome'),
            },
            {
                path: '/management/bgSingerManagement',
                name: 'bgSingerManagement',
                meta: {bgEnterCheck: true, title: '后台管理 歌手'},
                component: () => import('../views/background/BgSingerManagement')
            },
            {
                path: '/management/bgSongManagement',
                name: 'bgSongManagement',
                meta: {bgEnterCheck: true, title: '后台管理 曲库'},
                component: () => import('../views/background/BgSongManagement')
            },
            {
                path: '/management/bgUserManagement',
                name: 'bgUserManagement',
                meta: {bgEnterCheck: true, title: '后台管理 用户'},
                component: () => import('../views/background/BgUserManagement')
            },
        ],
    },
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

// 后台路由守卫
router.beforeEach((to, from, next) => {
    window.document.title = to.meta.title
    if (to.meta.bgEnterCheck) { // 访问受限的后台页面
        request.get('/admin/isLogin')
            .then(res => {
                if (res.code === '1') {
                    next()
                } else {
                    ElMessage({
                        showClose: true,
                        type: 'error',
                        message: res.msg,
                        center: true,
                    });
                    window.sessionStorage.clear()
                    next('/management/login')
                }
            })
            .catch(error => {
                console.log(error)
                ElMessage({
                    showClose: true,
                    type: 'error',
                    message: '连接服务器失败',
                    center: true,
                });
                next('/management/login')
            })
    } else { // 进入不受限的页面
        next()
    }
})

export default router
