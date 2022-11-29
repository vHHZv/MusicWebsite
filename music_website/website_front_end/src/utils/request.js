import axios from 'axios'

const request = axios.create({
    baseURL: 'http://localhost:8081',  // 注意！！ 这里是全局统一加上了 后端接口前缀，后端必须进行跨域配置！
    timeout: 5000
})

// request 拦截器
// 可以自请求发送前对请求做一些处理
// 比如统一加token，对请求参数统一加密
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    // config.withCredentials=true // 设置cross跨域，并设置访问权限，允许跨域携带cookie信息，启用 session
    config.headers['Admin-Token'] = window.sessionStorage.getItem("admin");  // 设置请求头
    return config
}, error => {
    return Promise.reject(error)
});

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        let res = response.data;
        // 如果是返回的文件
        if (response.config.responseType === 'blob') {
            return res
        }
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        return res;
    },
    error => {
        console.log('err' + error) // for debug
        return Promise.reject(error)
    }
)


export default request