package music.website_backend.controller;

import music.website_backend.controller.utils.ResultUtil;
import music.website_backend.service.impl.ImageServiceImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/image")
public class ImageController {
    private final ImageServiceImpl imageService;

    public ImageController(ImageServiceImpl imageService) {
        this.imageService = imageService;
    }

    /**
     * 图片上传
     */
    @PostMapping("/upload")
    public ResultUtil<?> uploadImage(MultipartFile file) {
        Long fileIndex = imageService.upload(file);
        // 成功返回新图片 image_id 则为上传成功
        if (fileIndex != 0L) {
            return ResultUtil.success("图片上传成功", String.valueOf(fileIndex));
        }
        return ResultUtil.error("图片上传失败");
    }

    /**
     * 图片下载
     */
    @GetMapping("/download/{filename}")
    public void downloadImage(@PathVariable String filename, HttpServletResponse response) {
        imageService.download(filename, response);
    }
}
