package music.website_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_song")
public class Song {
    @TableId(type = IdType.AUTO)
    private Long songId;
    private String songName;
    @TableField(exist = false)
    private String singerName;
    private Long cover;
    private String info;
    private Long mp3;
    private String lyrics;
    private Integer deleted;
}
