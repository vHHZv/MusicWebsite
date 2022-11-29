package music.website_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_admin")
public class Admin {
    @TableId(type = IdType.AUTO)
    private Long aid;
    private String adminName;
    private String passwd;
}
