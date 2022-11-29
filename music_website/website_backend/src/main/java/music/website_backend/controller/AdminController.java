package music.website_backend.controller;

import music.website_backend.controller.utils.ResultUtil;
import music.website_backend.entity.Admin;
import music.website_backend.service.impl.AdminServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminServiceImpl adminService;

    public AdminController(AdminServiceImpl adminService) {
        this.adminService = adminService;
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public ResultUtil<?> login(@RequestBody Admin admin) {
        // 调用业务层
        String token = adminService.login(admin);
        // 查询到管理员存在且账号密码正确
        if (token != null) {
            return ResultUtil.success("欢迎您，管理员 " + admin.getAdminName(), token);
        } else {
            return ResultUtil.error("账户名或密码错误");
        }
    }

    /**
     * 判断管理员是否登录
     */
    @GetMapping("/isLogin")
    public ResultUtil<?> isLogin(HttpServletRequest request) {
        String token = request.getHeader("Admin-Token");
        HashMap<Boolean, String> map = adminService.isLogin(token);
        if (token != null && !token.equals("null") && map.containsKey(true)) {
            return ResultUtil.success();
        }
        return ResultUtil.error(map.get(false));
    }

    /**
     * 解析管理员名
     */
    @GetMapping("/getAdminName")
    public ResultUtil<?> getAdminName(HttpServletRequest request) {
        return ResultUtil.success(adminService.getAdminName(request.getHeader("Admin-Token")));
    }
}
