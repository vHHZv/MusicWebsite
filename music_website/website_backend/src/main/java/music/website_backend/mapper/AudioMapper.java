package music.website_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import music.website_backend.entity.Audio;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AudioMapper extends BaseMapper<Audio> {
}
