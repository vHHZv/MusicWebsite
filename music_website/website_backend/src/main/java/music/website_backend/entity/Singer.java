package music.website_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_singer")
public class Singer {
    @TableId(type = IdType.AUTO)
    private Long sid;
    private String singerName;
    private String alias;
    private Long avatar;
    private String info;
    private Integer deleted;
}
