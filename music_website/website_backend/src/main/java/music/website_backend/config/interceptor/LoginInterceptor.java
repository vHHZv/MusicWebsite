package music.website_backend.config.interceptor;

import cn.hutool.json.JSONUtil;
import music.website_backend.controller.utils.ResultUtil;
import music.website_backend.service.impl.AdminServiceImpl;
import music.website_backend.service.impl.UserServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    private final AdminServiceImpl adminService;
    private final UserServiceImpl userService;

    public LoginInterceptor(AdminServiceImpl adminService, UserServiceImpl userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String adminToken = request.getHeader("Admin-Token");
        String userToken = request.getHeader("User-Token");
        HashMap<Boolean, String> map;
        if (adminToken != null) {
            map = adminService.isLogin(adminToken);
        } else {
            map = userService.isLogin(userToken);
        }
        if (map.containsKey(false)) {
            String json = JSONUtil.toJsonStr(ResultUtil.error(map.get(false)));
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.addHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
            response.getWriter().write(json);
            return false;
        }
        return true;
    }
}
