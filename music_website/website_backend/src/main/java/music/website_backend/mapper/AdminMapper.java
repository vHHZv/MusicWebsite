package music.website_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import music.website_backend.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
