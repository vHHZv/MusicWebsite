package music.website_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import music.website_backend.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 向 tb_user_song 表插入数据
     */
    @Insert("insert into tb_user_song(user, song) values(#{user}, #{song})")
    int addTbUserSong(Long user, Long song);

    /**
     * 向 tb_user_song 表删除项
     */
    @Delete("delete from tb_user_song where user=#{user} and song=#{song}")
    int removeTbUserSong(Long user, Long song);

    /**
     * 获取用户收藏的单曲 song_id 列表
     */
    @Select("select song from tb_user_song where user=#{uid}")
    List<Long> selectSongIdsByUid(Long uid);

    /**
     * 查询用户 tb_user_song 表中是否存在 uid、songId 对
     */
    @Select("select exists (select * from tb_user_song where user=#{uid} and song=#{songId})")
    boolean isUidAndSongIdIn(Long uid, Long songId);
}
