package music.website_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import music.website_backend.entity.Singer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SingerMapper extends BaseMapper<Singer> {
    /**
     * 根据歌手 id 获取单曲 id
     */
    @Select("select song from tb_singer_song where singer=#{singer}")
    List<Long> selectSongIdsBySingerId(Long singer);
}
