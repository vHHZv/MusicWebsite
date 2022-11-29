package music.website_backend.controller;

import music.website_backend.controller.utils.ResultUtil;
import music.website_backend.entity.Singer;
import music.website_backend.service.impl.SingerServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/singer")
public class SingerController {
    private final SingerServiceImpl singerService;

    public SingerController(SingerServiceImpl singerService) {
        this.singerService = singerService;
    }

    /**
     * 获取所有歌手信息
     */
    @GetMapping("/all")
    public ResultUtil<?> getAllByPage(@RequestParam int currentPage, @RequestParam int pageSize) {
        Map<String, Object> result = singerService.getAllByPage(currentPage, pageSize);
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
                                        @RequestParam String singerName, @RequestParam String alias) {
        Map<String, Object> result = singerService.getAllBySearchPage(currentPage, pageSize, singerName, alias);
        if (!result.isEmpty()) {
            return ResultUtil.success(result);
        }
        return ResultUtil.error("根据查询条件，未能找到匹配歌手");
    }

    /**
     * 管理员页面新增歌手
     */
    @PostMapping("/addSinger")
    public ResultUtil<?> addSinger(@RequestBody Singer singer) {
        int inserted = singerService.addSingerByAdmin(singer);
        if (inserted == 1) {
            return ResultUtil.success("已成功添加新歌手");
        }
        return ResultUtil.error("新增失败");
    }

    /**
     * 修改歌手数据
     */
    @PutMapping("/modifySinger")
    public ResultUtil<?> modifySinger(@RequestBody Singer singer) {
        int modifySinger = singerService.modifySinger(singer);
        if (modifySinger == 1) {
            return ResultUtil.success("已成功修改歌手信息");
        }
        return ResultUtil.error("修改失败");
    }

    /**
     * 删除歌手
     */
    @DeleteMapping("/toggleState/{id}/{state}")
    public ResultUtil<?> toggleState(@PathVariable("id") Long id, @PathVariable("state") int state) {
        int deleted = singerService.toggleState(id, state);
        if (deleted == 1) {
            return ResultUtil.success("已成功更新歌手状态");
        }
        return ResultUtil.error("歌手状态更新失败");
    }

    /**
     * 获取单个歌手数据
     */
    @GetMapping("/getOneSinger/{sid}")
    public ResultUtil<?> getOneSingerInfo(@PathVariable("sid") Long sid) {
        Map<String, Object> singerInfo = singerService.getOneSingerInfo(sid);
        if (singerInfo != null) {
            return ResultUtil.success(singerInfo);
        }
        return ResultUtil.error("获取歌手信息失败");
    }
}
