package music.website_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import music.website_backend.entity.Admin;

import java.util.HashMap;

public interface AdminService extends IService<Admin> {
    /**
     * 管理员登录
     */
    String login(Admin admin);

    /**
     * 判断管理员是否登录
     */
    HashMap<Boolean, String> isLogin(String token);

    /**
     * 从 token 中获取管理员姓名
     */
    Object getAdminName(String token);
}
