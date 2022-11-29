package music.website_backend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.json.JSONException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import music.website_backend.service.utils.FileOpUtil;
import music.website_backend.entity.User;
import music.website_backend.entity.utils.UserReflectUtil;
import music.website_backend.mapper.UserMapper;
import music.website_backend.service.UserService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final UserMapper userMapper;
    private final ImageServiceImpl imageService;
    private final SongServiceImpl songService;

    public UserServiceImpl(UserMapper userMapper, ImageServiceImpl imageService, SongServiceImpl songService) {
        this.userMapper = userMapper;
        this.imageService = imageService;
        this.songService = songService;
    }

    /**
     * 用户注册
     */
    @Override
    public Boolean register(User user) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.exists(user.getUsername() != null && !user.getUsername().isEmpty(),
                "select * from tb_user where username='" + user.getUsername() + "'");
        List<User> users = userMapper.selectList(lqw);
        if (users.isEmpty()) {
            userMapper.insert(user);
            return true;
        }
        return false;
    }

    /**
     * 用户登录
     */
    @Override
    public String login(User user) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(user.getUsername() != null, User::getUsername, user.getUsername())
                .eq(user.getPasswd() != null, User::getPasswd, user.getPasswd());
        User selected = userMapper.selectOne(lqw);
        if (selected != null) {
            // 创建JWT Token
            return JWT.create()
                    .setPayload("uid", String.valueOf(selected.getUid()))
                    .setPayload("username", String.valueOf(selected.getUsername()))
                    .setExpiresAt(DateUtil.offsetHour(DateUtil.date(), 8))
                    .setKey("hhzJLU".getBytes())
                    .sign();
        }
        return null;
    }

    /**
     * 判断用户是否登录
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
            JWTValidator.of(token).validateAlgorithm(JWTSignerUtil.hs256("hhzJLU".getBytes()));
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
        // 检查用户是否存在
        User user = userMapper.selectById((Serializable) jwt.getPayload("uid"));
        return new HashMap<Boolean, String>() {{
            put(user != null, "用户信息验证失败");
        }};
    }

    /**
     * 从 token 中获取用户姓名
     */
    @Override
    public Object getUsername(String token) {
        return JWTUtil.parseToken(token).getPayload("username");
    }

    /**
     * 按页查询所有信息并展示在表格中
     */
    @Override
    public Map<String, Object> getAllByPage(int currentPage, int pageSize) {
        IPage<User> page = new Page<>(currentPage, pageSize);
        userMapper.selectPage(page, null);
        // 没有数据
        if (page.getTotal() == 0) {
            return null;
        }
        return pageHandler(page);
    }

    /**
     * 分页结果处理
     */
    @Override
    public Map<String, Object> pageHandler(IPage<User> page) {
        // 返回结果 Map
        HashMap<String, Object> resultMap = new HashMap<>();
        // 分页数据集
        List<User> records = page.getRecords();
        if (!records.isEmpty()) {
            // 图片索引集
            HashSet<Long> imgIndexes = new HashSet<>();
            // 将图片索引放入索引集
            records.forEach(item -> imgIndexes.add(item.getAvatar()));
            // 调用 ImageService 查询图片路径
            Map<Long, String> imageFilePaths = imageService.getAllFilenameByIdList(imgIndexes);

            // 存储 User 对象转 List 的结果集
            ArrayList<Map<String, Object>> recordList = new ArrayList<>();
            // 将 User 对象转为 List 并格式化
            records.forEach(item -> {
                Map<String, Object> map = BeanUtil.beanToMap(item);
                map = userMapOutputToTableFormat(map, item, imageFilePaths);
                recordList.add(map);
            });
            resultMap.put("data", recordList); // 数据
            resultMap.put("pageSize", page.getSize()); // 每页显示数
            resultMap.put("currentPage", page.getCurrent()); // 当前页数
            resultMap.put("totalPage", page.getPages()); // 总页数
        }
        return resultMap;
    }

    /**
     * User 的 Map 对象输出到表格的格式化
     */
    @Override
    public Map<String, Object> userMapOutputToTableFormat(Map<String, Object> map, User user, Map<Long, String> imageFilePaths) {
        map.replace("uid", String.valueOf(map.get("uid")));
        if (user.getPhoneNum() == null) {
            map.replace("phoneNum", "");
        }
        if (user.getEmail() == null) {
            map.replace("email", "");
        }
        if (user.getBirthday() != null) {
            // birthday 非空时，修改 birthday 格式为 yyyy/MM/dd
            map.replace("birthday", new SimpleDateFormat("yyyy-MM-dd").format(user.getBirthday()));
        } else {
            map.replace("birthday", "");
        }

        // 图片有默认值，非空，更改图片索引为图片路径，更改键值为 image
        map.remove("avatar");
        // 获取文件名
        String imageFilePath = imageFilePaths.get(user.getAvatar());
        // 对外网连接做特殊处理
        if (!imageFilePath.startsWith("http")) {
            imageFilePath = FileOpUtil.BASE_URL + "/image/download/" + imageFilePath;
        }
        // 更改键值为 image
        map.put("image", imageFilePath);

        if (user.getInfo() == null) {
            map.replace("info", "");
        }
        if (user.getDeleted() == 0) {
            map.replace("deleted", "正常");
        } else {
            map.replace("deleted", "注销");
        }
        return map;
    }

    /**
     * 按页模糊查询并展示在表格中
     */
    @Override
    public Map<String, Object> getAllBySearchPage(int currentPage, int pageSize, String username, String phoneNum, String email) {
        IPage<User> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            lqw.like(User::getUsername, username);
        }
        if (phoneNum != null && !phoneNum.isEmpty()) {
            lqw.like(User::getPhoneNum, phoneNum);
        }
        if (email != null && !email.isEmpty()) {
            lqw.like(User::getEmail, email);
        }
        userMapper.selectPage(page, lqw);
        return pageHandler(page);
    }

    /**
     * 管理员新增用户
     */
    @Override
    public int addUserByAdmin(User user) {
        User format = userInsertFormat(user);
        if (format != null) {
            return userMapper.insert(format);
        }
        return 0;
    }

    /**
     * 格式化新增 User 对象
     */
    @Override
    public User userInsertFormat(User user) {
        // 获取属性名
        ArrayList<String> names = UserReflectUtil.getFieldNames();
        // 获取属性值
        HashMap<String, Object> values = UserReflectUtil.getFieldValues(user);
        // 获取方法
        HashMap<String, Method> methods = UserReflectUtil.getFieldMethods();
        for (String name : names) {
            if (values.get(name) == null || String.valueOf(values.get(name)).isEmpty())
                try {
                    switch (name) {
                        case "username":
                        case "passwd":
                            return null;
                        case "gender":
                            // 默认性别男
                            methods.get(name).invoke(user, "男");
                            break;
                        case "avatar":
                            // 设置默认用户头像
                            methods.get(name).invoke(user, 1L);
                            break;
                        case "phoneNum":
                        case "email":
                        case "birthday":
                        case "info":
                            methods.get(name).invoke(user, (Object) null);
                            break;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
        }
        return user;
    }

    /**
     * 修改用户数据
     */
    @Override
    public int modifyUser(User user) {
        LambdaUpdateWrapper<User> wrapper = userModifyFormat(user);
        return userMapper.update(null, wrapper);
    }

    /**
     * 格式化修改的用户数据
     */
    @Override
    public LambdaUpdateWrapper<User> userModifyFormat(User user) {
        // 获取属性名
        ArrayList<String> names = UserReflectUtil.getFieldNames();
        // 获取属性值
        HashMap<String, Object> values = UserReflectUtil.getFieldValues(user);
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        for (String name : names) {
            switch (name) {
                case "uid":
                    wrapper.eq(User::getUid, values.get(name));
                    break;
                case "username":
                    if (!String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(User::getUsername, values.get(name));
                    }
                    break;
                case "passwd":
                    if (!String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(User::getPasswd, values.get(name));
                    }
                    break;
                case "gender":
                    if (String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(User::getGender, "男");
                    } else {
                        wrapper.set(User::getGender, values.get(name));
                    }
                    break;
                case "avatar":
                    if (values.get(name) != null && !String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(User::getAvatar, values.get(name));
                    }
                    break;
                case "phoneNum":
                    if (String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(User::getPhoneNum, null);
                    } else {
                        wrapper.set(User::getPhoneNum, values.get(name));
                    }
                    break;
                case "email":
                    if (String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(User::getEmail, null);
                    } else {
                        wrapper.set(User::getEmail, values.get(name));
                    }
                    break;
                case "birthday":
                    if (values.get(name) == null) {
                        wrapper.set(User::getBirthday, null);
                    } else {
                        wrapper.set(User::getBirthday, values.get(name));
                    }
                    break;
                case "info":
                    if (String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(User::getInfo, null);
                    } else {
                        wrapper.set(User::getInfo, values.get(name));
                    }
                    break;
            }
        }
        return wrapper;
    }

    /**
     * 更新用户状态
     */
    @Override
    public int toggleState(Long id, int state) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        if (state == 0) {
            wrapper.eq(User::getUid, id).set(User::getDeleted, 1);
        } else {
            wrapper.eq(User::getUid, id).set(User::getDeleted, 0);
        }
        return userMapper.update(null, wrapper);
    }

    /**
     * 用户收藏单曲
     */
    @Override
    public int collectSong(Long user, Long song) {
        try {
            int num = userMapper.addTbUserSong(user, song);
            if (num != 0) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 用户取消收藏单曲
     */
    @Override
    public int clearCollectSong(Long user, Long song) {
        try {
            int num = userMapper.removeTbUserSong(user, song);
            if (num != 0) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取单个用户所有数据
     */
    @Override
    public Map<String, Object> getOneUserInfo(Long uid) {
        Object baseInfo = getOneUserBaseInfo(uid);
        if (baseInfo != null) {
            Object songs = getSongsByUid(uid);
            HashMap<String, Object> map = new HashMap<>();
            map.put("baseInfo", baseInfo);
            map.put("songs", songs);
            return map;
        }
        return null;
    }

    /**
     * 获取单个用户基础数据
     */
    @Override
    public Object getOneUserBaseInfo(Long uid) {
        IPage<User> page = new Page<>(1, 1);
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUid, uid).eq(User::getDeleted, 0);
        userMapper.selectPage(page, lqw);
        // 没有数据
        if (page.getTotal() == 0) {
            return null;
        }
        return ((List<?>) pageHandler(page).get("data")).get(0);
    }

    /**
     * 获取用户收藏的单曲信息
     */
    @Override
    public Object getSongsByUid(Long uid) {
        // 获取用户收藏的歌曲 id
        List<Long> songIds = userMapper.selectSongIdsByUid(uid);
        if (!songIds.isEmpty()) {
            return songService.getSongsBySongIdList(songIds);
        }
        return null;
    }

    /**
     * 判断用户是否收藏某单曲
     */
    @Override
    public boolean isCollected(Long user, Long song) {
        return userMapper.isUidAndSongIdIn(user, song);
    }
}
