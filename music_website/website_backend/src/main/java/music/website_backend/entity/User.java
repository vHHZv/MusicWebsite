package music.website_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long uid;
    private String username;
    private String passwd;
    private String gender;
    private String phoneNum;
    private String email;
    private Date birthday;
    private Long avatar;
    private String info;
    private Integer deleted;
}
