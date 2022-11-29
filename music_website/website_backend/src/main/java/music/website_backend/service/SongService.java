package music.website_backend.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import music.website_backend.entity.Song;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SongService extends IService<Song> {
    /**
     * 根据 songIds 列表查询单曲
     */
    Object getSongsBySongIdList(List<Long> songIds);

    /**
     * 按页查询所有信息并展示在表格中
     */
    Map<String, Object> getAllByPage(int currentPage, int pageSize);

    /**
     * 分页结果处理
     */
    Map<String, Object> pageHandler(IPage<Song> page);

    /**
     * Song 的 Map 对象输出到表格的格式化
     */
    Map<String, Object> songMapOutputToTableFormat(Map<String, Object> map, Song song, Map<Long, String> imageFilePaths, Map<Long, String> audioFilePaths, HashMap<Long, String> songIdSingerNameMapping);

    /**
     * 按页模糊查询并展示在表格中
     */
    Map<String, Object> getAllBySearchPage(int currentPage, int pageSize, String songName, String lyrics);

    /**
     * 管理员新增单曲
     */
    int addSongByAdmin(Song song, String singerName);

    /**
     * 格式化新增 Song 对象
     */
    Song songInsertFormat(Song song);

    /**
     * 修改单曲数据
     */
    int modifySong(Song song);

    /**
     * 格式化修改的单曲数据
     */
    LambdaUpdateWrapper<Song> songModifyFormat(Song song);

    /**
     * 更新单曲状态
     */
    int toggleState(Long id, int state);

    /**
     * 获取单个单曲数据
     */
    Object getOneSongInfo(Long songId);
}
