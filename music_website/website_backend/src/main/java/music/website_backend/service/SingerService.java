package music.website_backend.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import music.website_backend.entity.Singer;

import java.util.*;

public interface SingerService extends IService<Singer> {
    /**
     * 根据姓名查找歌手
     */
    Long selectSingerByName(String name);

    /**
     * 按页查询所有信息并展示在表格中
     */
    Map<String, Object> getAllByPage(int currentPage, int pageSize);

    /**
     * 分页结果处理
     */
    Map<String, Object> pageHandler(IPage<Singer> page);

    /**
     * Singer 的 Map 对象输出到表格的格式化
     */
    Map<String, Object> singerMapOutputToTableFormat(Map<String, Object> map, Singer singer, Map<Long, String> imageFilePaths);

    /**
     * 按页模糊查询并展示在表格中
     */
    Map<String, Object> getAllBySearchPage(int currentPage, int pageSize, String singerName, String alias);

    /**
     * 管理员新增用户
     */
    int addSingerByAdmin(Singer singer);

    /**
     * 格式化新增 Singer 对象
     */
    Singer singerInsertFormat(Singer singer);

    /**
     * 修改歌手数据
     */
    int modifySinger(Singer singer);

    /**
     * 格式化修改的歌手数据
     */
    LambdaUpdateWrapper<Singer> singerModifyFormat(Singer singer);

    /**
     * 更新歌手状态
     */
    int toggleState(Long id, int state);

    /**
     * 获取歌手所有信息
     */
    Map<String, Object> getOneSingerInfo(Long sid);

    /**
     * 获取单个歌手基础数据
     */
    Object getOneSingerBaseInfo(Long sid);

    /**
     * 获取歌手的单曲信息
     */
    Object getSongsBySingerId(Long sid);

    /**
     * 根据传入的 id 查询歌手名
     */
    Map<Long, String> getSingerNameBySongId(Set<Long>indexes);
}
