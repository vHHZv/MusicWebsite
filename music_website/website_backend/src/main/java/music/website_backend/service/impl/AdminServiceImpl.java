package music.website_backend.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.json.JSONException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import music.website_backend.entity.Admin;
import music.website_backend.mapper.AdminMapper;
import music.website_backend.service.AdminService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    private final AdminMapper adminMapper;

    public AdminServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    /**
     * 管理员登录
     */
    @Override
    public String login(Admin admin) {
        LambdaQueryWrapper<Admin> lqw = new LambdaQueryWrapper<>();
        lqw.eq(admin.getAdminName() != null, Admin::getAdminName, admin.getAdminName())
                .eq(admin.getPasswd() != null, Admin::getPasswd, admin.getPasswd());
        Admin selected = adminMapper.selectOne(lqw);
        if (selected != null) {
            // 创建JWT Token
            return JWT.create()
                    .setPayload("aid", String.valueOf(selected.getAid()))
                    .setPayload("adminName", String.valueOf(selected.getAdminName()))
                    .setExpiresAt(DateUtil.offsetHour(DateUtil.date(), 8))
                    .setKey("hhz".getBytes())
                    .sign();
        }
        return null;
    }

    /**
     * 判断管理员是否登录
     */
    @Override
    public HashMap<Boolean, String> isLogin(String token) {
        if (token == null || token.equals("null")) {
            return new HashMap<Boolean, String>() {{
                put(false, "请先完成登录");
            }};
        }
        try {
            // 验证算法并判断 token 是否被篡改
            JWTValidator.of(token).validateAlgorithm(JWTSignerUtil.hs256("hhz".getBytes()));
        } catch (ValidateException e) {
            return new HashMap<Boolean, String>() {{
                put(false, "签名验证失败");
            }};
        } catch (JSONException e) {
            return new HashMap<Boolean, String>() {{
                put(false, "token解析失败");
            }};
        }
        try {
            JWTValidator.of(token).validateDate();
        } catch (ValidateException e) {
            return new HashMap<Boolean, String>() {{
                put(false, "登录超时");
            }};
        }
        final JWT jwt = JWTUtil.parseToken(token);
        // 检查管理员是否存在
        Admin admin = adminMapper.selectById((Serializable) jwt.getPayload("aid"));
        return new HashMap<Boolean, String>() {{
            put(admin != null, "管理员信息验证失败");
        }};
    }

    /**
     * 从 token 中获取管理员姓名
     */
    @Override
    public Object getAdminName(String token) {
        return JWTUtil.parseToken(token).getPayload("adminName");
    }
}
