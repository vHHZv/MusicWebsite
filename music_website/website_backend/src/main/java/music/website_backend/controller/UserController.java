package music.website_backend.controller;

import music.website_backend.controller.utils.ResultUtil;
import music.website_backend.entity.User;
import music.website_backend.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResultUtil<?> register(@RequestBody User user) {
        // 调用业务层进行用户注册
        if (userService.register(user)) {
            return ResultUtil.success("注册成功");
        } else {
            return ResultUtil.error("账户名已存在");
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResultUtil<?> login(@RequestBody User user) {
        // 调用业务层
        String token = userService.login(user);
        // 查询到用户存在且账号密码正确
        if (token != null) {
            return ResultUtil.success("欢迎您， " + user.getUsername(), token);
        } else {
            return ResultUtil.error("账户名或密码错误");
        }
    }

    /**
     * 判断用户是否登录
     */
    @GetMapping("/isLogin")
    public ResultUtil<?> isLogin(HttpServletRequest request) {
        String token = request.getHeader("User-Token");
        HashMap<Boolean, String> map = userService.isLogin(token);
        if (token != null && !token.equals("null") && map.containsKey(true)) {
            return ResultUtil.success();
        }
        return ResultUtil.error(map.get(false));
    }

    /**
     * 解析用户名
     */
    @GetMapping("/getUsername")
    public ResultUtil<?> getUsername(HttpServletRequest request) {
        return ResultUtil.success(userService.getUsername(request.getHeader("User-Token")));
    }

    /**
     * 获取所有用户信息
     */
    @GetMapping("/all")
    public ResultUtil<?> getAllByPage(@RequestParam int currentPage, @RequestParam int pageSize) {
        Map<String, Object> result = userService.getAllByPage(currentPage, pageSize);
        if (result != null) {
            return ResultUtil.success(result);
        }
        return ResultUtil.error("暂无数据");
    }

    /**
     * 管理员页面检索
     */
    @GetMapping("/searchAll")
    public ResultUtil<?> getAllBySearch(@RequestParam int currentPage, @RequestParam int pageSize,
                                        @RequestParam String username, @RequestParam String phoneNum,
                                        @RequestParam String email) {
        Map<String, Object> result = userService.getAllBySearchPage(currentPage, pageSize, username, phoneNum, email);
        if (!result.isEmpty()) {
            return ResultUtil.success(result);
        }
        return ResultUtil.error("根据查询条件，未能找到匹配用户");
    }

    /**
     * 管理员页面新增用户
     */
    @PostMapping("/addUser")
    public ResultUtil<?> addUser(@RequestBody User user) {
        int inserted = userService.addUserByAdmin(user);
        if (inserted == 1) {
            return ResultUtil.success("已成功添加新用户");
        }
        return ResultUtil.error("新增失败");
    }

    /**
     * 修改用户数据
     */
    @PutMapping("/modifyUser")
    public ResultUtil<?> modifyUser(@RequestBody User user) {
       int modifyUser = userService.modifyUser(user);
       if (modifyUser == 1) {
           return ResultUtil.success("已成功修改用户信息");
        }
        return ResultUtil.error("修改失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/toggleState/{id}/{state}")
    public ResultUtil<?> toggleState(@PathVariable("id") Long id, @PathVariable("state") int state) {
        int deleted = userService.toggleState(id, state);
        if (deleted == 1) {
            return ResultUtil.success("已成功更新账户状态");
        }
        return ResultUtil.error("账户状态更新失败");
    }

    /**
     * 用户收藏单曲
     */
    @PutMapping("/collectSong")
    public ResultUtil<?> collectSong(@RequestParam("user") Long user, @RequestParam("song") Long song) {
        int collect = userService.collectSong(user, song);
        if (collect == 1) {
            return ResultUtil.success("收藏成功");
        }
        return ResultUtil.error("收藏失败");
    }

    /**
     * 用户取消收藏单曲
     */
    @DeleteMapping("/clearCollectSong")
    public ResultUtil<?> clearCollectSong(@RequestParam("user") Long user, @RequestParam("song") Long song) {
        int collect = userService.clearCollectSong(user, song);
        if (collect == 1) {
            return ResultUtil.success("已取消收藏");
        }
        return ResultUtil.error("取消收藏失败");
    }

    /**
     * 获取单个用户数据
     */
    @GetMapping("/getOneUser/{uid}")
    public ResultUtil<?> getOneUserInfo(@PathVariable("uid") Long uid) {
        Map<String, Object> userInfo = userService.getOneUserInfo(uid);
        if (userInfo != null) {
            return ResultUtil.success(userInfo);
        }
        return ResultUtil.error("获取用户信息失败");
    }

    /**
     * 判断用户是否有收藏某单曲
     */
    @GetMapping("/isCollected")
    public ResultUtil<?> isCollected(@RequestParam("user") Long user, @RequestParam("song") Long song) {
        boolean collected = userService.isCollected(user, song);
        return ResultUtil.success(collected);
    }
}
