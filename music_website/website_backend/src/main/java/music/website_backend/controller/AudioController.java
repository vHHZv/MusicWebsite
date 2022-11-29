package music.website_backend.controller;

import music.website_backend.controller.utils.ResultUtil;
import music.website_backend.service.impl.AudioServiceImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/audio")
public class AudioController {
    private final AudioServiceImpl audioService;

    public AudioController(AudioServiceImpl audioService) {
        this.audioService = audioService;
    }

    /**
     * 音频上传
     */
    @PostMapping("/upload")
    public ResultUtil<?> uploadAudio(MultipartFile file) {
        Long fileIndex = audioService.upload(file);
        // 成功返回新音频 audio_id 则为上传成功
        if (fileIndex != 0L) {
            return ResultUtil.success("音频上传成功", String.valueOf(fileIndex));
        }
        return ResultUtil.error("音频上传失败");
    }

    /**
     * 图片下载
     */
    @GetMapping("/download/{filename}")
    public void downloadImage(@PathVariable String filename, HttpServletResponse response) {
        audioService.download(filename, response);
    }
}
