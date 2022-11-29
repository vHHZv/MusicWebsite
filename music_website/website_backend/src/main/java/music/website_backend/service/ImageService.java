package music.website_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import music.website_backend.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

public interface ImageService extends IService<Image> {
    /**
     * 根据传入的 id 列表查询数据库图片名字段
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
