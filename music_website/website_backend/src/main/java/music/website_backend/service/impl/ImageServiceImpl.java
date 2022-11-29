package music.website_backend.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import music.website_backend.service.utils.FileOpUtil;
import music.website_backend.entity.Image;
import music.website_backend.mapper.ImageMapper;
import music.website_backend.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {
    private final ImageMapper imageMapper;

    public ImageServiceImpl(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    /**
     * 根据传入的 id 列表查询数据库图片名字段
     */
    @Override
    public Map<Long, String> getAllFilenameByIdList(Set<Long> indexes) {
        LambdaQueryWrapper<Image> lqw = new LambdaQueryWrapper<>();
        // 根据传入的主键 id 查询 id、filename
        lqw.select(Image::getImageId, Image::getFilename).in(Image::getImageId, indexes);
        // 查询
        List<Image> imageList = imageMapper.selectList(lqw);
        HashMap<Long, String> filenameMap = new HashMap<>();
        // 抽取 filename 为 Map
        imageList.forEach(item -> filenameMap.put(item.getImageId(), item.getFilename()));
        return filenameMap;
    }

    /**
     * 上传
     */
    @Override
    public Long upload(MultipartFile file) {
        // 计算 MD5
        String md5Hex = null;
        try {
            md5Hex = DigestUtil.md5Hex(new ByteArrayInputStream(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        LambdaQueryWrapper<Image> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Image::getMd5, md5Hex);
        Image image = imageMapper.selectOne(lqw);
        if (image == null) {
            String filename = FileOpUtil.writeImg(file);
            Image newImage = new Image(filename, md5Hex);
            int insert = imageMapper.insert(newImage);
            if (insert == 1) {
                return newImage.getImageId();
            }
            return 0L;
        } else {
            return image.getImageId();
        }
    }

    /**
     * 下载
     */
    @Override
    public void download(String filename, HttpServletResponse response) {
        if (filename != null && !filename.isEmpty()) {
            FileOpUtil.readImg(filename, response);
        }
    }
}
