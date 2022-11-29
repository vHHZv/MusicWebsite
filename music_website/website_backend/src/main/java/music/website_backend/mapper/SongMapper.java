package music.website_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import music.website_backend.entity.Song;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Mapper
public interface SongMapper extends BaseMapper<Song> {
    /**
     * 向 tb_singer_song 表插入数据
     */
    @Insert("insert into tb_singer_song(singer, song) values(#{singer}, #{song})")
    void addTbSingerSong(Long singer, Long song);

    /**
     * 根据 songIdSet 在 tb_singer_song 表查找 singerId
     */
    @Select({
            "<script>",
            "select",
            "song, singer",
            "from tb_singer_song",
            "where song in",
            "<foreach collection='set' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    List<HashMap<String, Long>> selectSingerIdsBySongIdSet(HashSet<Long> set);

    /**
     * 根据 songId 修改 singer
     */
    @Update("update tb_singer_song set singer=#{singer} where song=#{song}")
    void updateSinger(Long song, Long singer);
}
