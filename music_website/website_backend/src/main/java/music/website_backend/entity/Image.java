package music.website_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_image")
public class Image {
    @TableId(type = IdType.AUTO)
    private Long imageId;
    private String filename;
    private String md5;
    private String info;
    private Integer deleted;

    public Image(String filename, String md5) {
        this.filename = filename;
        this.md5 = md5;
    }
}
