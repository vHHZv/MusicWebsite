package music.website_backend.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import music.website_backend.entity.User;

import java.util.HashMap;
import java.util.Map;

public interface UserService extends IService<User> {
    /**
     * 用户注册
     */
    Boolean register(User user);

    /**
     * 用户登录
     */
    String login(User user);

    /**
     * 判断用户是否登录
     */
    HashMap<Boolean, String> isLogin(String token);

    /**
     * 从 token 中获取用户姓名
     */
    Object getUsername(String token);

    /**
     * 按页查询所有信息并展示在表格中
     */
    Map<String, Object> getAllByPage(int currentPage, int pageSize);

    /**
     * 分页结果处理
     */
    Map<String, Object> pageHandler(IPage<User> page);

    /**
     * User 的 Map 对象输出到表格的格式化
     */
    Map<String, Object> userMapOutputToTableFormat(Map<String, Object> map, User user, Map<Long, String> imageFilePaths);

    /**
     * 按页模糊查询并展示在表格中
     */
    Map<String, Object> getAllBySearchPage(int currentPage, int pageSize, String username, String phoneNum, String email);

    /**
     * 管理员新增用户
     */
    int addUserByAdmin(User user);

    /**
     * 格式化新增 User 对象
     */
    User userInsertFormat(User user);

    /**
     * 修改用户数据
     */
    int modifyUser(User user);

    /**
     * 格式化修改的用户数据
     */
    LambdaUpdateWrapper<User> userModifyFormat(User user);

    /**
     * 更新用户状态
     */
    int toggleState(Long id, int state);

    /**
     * 用户收藏单曲
     */
    int collectSong(Long user, Long song);

    /**
     * 用户取消收藏单曲
     */
    int clearCollectSong(Long user, Long song);

    /**
     * 获取单个用户所有数据
     */
    Map<String, Object> getOneUserInfo(Long uid);

    /**
     * 获取单个用户基础数据
     */
    Object getOneUserBaseInfo(Long uid);

    /**
     * 获取用户收藏的单曲信息
     */
    Object getSongsByUid(Long uid);

    /**
     * 判断用户是否收藏某单曲
     */
    boolean isCollected(Long user, Long song);
}
