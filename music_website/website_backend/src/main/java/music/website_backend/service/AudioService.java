package music.website_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import music.website_backend.entity.Audio;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

public interface AudioService extends IService<Audio> {
    /**
     * 根据传入的 id 列表查询数据库音频名字段
     */
    Map<Long, String> getAllFilenameByIdList(Set<Long> indexes);

    /**
     * 上传
     */
    Long upload(MultipartFile file);

    /**
     * 下载
     */
    void download(String filename, HttpServletResponse response);
}
