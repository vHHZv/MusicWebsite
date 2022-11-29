package music.website_backend.controller;

import music.website_backend.controller.utils.ResultUtil;
import music.website_backend.entity.Song;
import music.website_backend.service.impl.SongServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/song")
public class SongController {
    private final SongServiceImpl songService;

    public SongController(SongServiceImpl songService) {
        this.songService = songService;
    }

    /**
     * 获取所有单曲信息
     */
    @GetMapping("/all")
    public ResultUtil<?> getAllByPage(@RequestParam int currentPage, @RequestParam int pageSize) {
        Map<String, Object> result = songService.getAllByPage(currentPage, pageSize);
        if (result != null) {
            return ResultUtil.success(result);
        }
        return ResultUtil.error("暂无数据");
    }

    /**
     * 管理员页面检索
     */
    @GetMapping("/searchAll")
    public ResultUtil<?> getAllBySearch(@RequestParam int currentPage, @RequestParam int pageSize,
                                        @RequestParam String songName, @RequestParam String lyrics) {
        Map<String, Object> result = songService.getAllBySearchPage(currentPage, pageSize, songName, lyrics);
        if (!result.isEmpty()) {
            return ResultUtil.success(result);
        }
        return ResultUtil.error("根据查询条件，未能找到匹配单曲");
    }

    /**
     * 管理员页面新增单曲
     */
    @PostMapping("/addSong")
    public ResultUtil<?> addSong(@RequestBody Song song) {
        int inserted = songService.addSongByAdmin(song, song.getSingerName());
        if (inserted == 1) {
            return ResultUtil.success("已成功添加新单曲");
        }
        return ResultUtil.error("新增失败");
    }

    /**
     * 修改单曲数据
     */
    @PutMapping("/modifySong")
    public ResultUtil<?> modifySong(@RequestBody Song song) {
        int modifySong = songService.modifySong(song);
        if (modifySong == 1) {
            return ResultUtil.success("已成功修改单曲信息");
        }
        return ResultUtil.error("修改失败");
    }

    /**
     * 删除单曲
     */
    @DeleteMapping("/toggleState/{id}/{state}")
    public ResultUtil<?> toggleState(@PathVariable("id") Long id, @PathVariable("state") int state) {
        int deleted = songService.toggleState(id, state);
        if (deleted == 1) {
            return ResultUtil.success("已成功更新单曲状态");
        }
        return ResultUtil.error("单曲状态更新失败");
    }

    /**
     * 获取单个单曲数据
     */
    @GetMapping("/getOneSong/{songId}")
    public ResultUtil<?> getOneSongInfo(@PathVariable("songId") Long songId) {
        Object song = songService.getOneSongInfo(songId);
        if (song != null) {
            return ResultUtil.success(song);
        }
        return ResultUtil.error("获取单曲信息失败");
    }
}
