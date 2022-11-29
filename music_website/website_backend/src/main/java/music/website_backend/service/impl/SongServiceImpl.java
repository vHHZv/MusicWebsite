package music.website_backend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import music.website_backend.service.utils.FileOpUtil;
import music.website_backend.entity.Song;
import music.website_backend.entity.utils.SongReflectUtil;
import music.website_backend.mapper.SongMapper;
import music.website_backend.service.SongService;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Service
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements SongService {
    private final SongMapper songMapper;
    private final SingerServiceImpl singerService;
    private final ImageServiceImpl imageService;
    private final AudioServiceImpl audioService;

    public SongServiceImpl(SongMapper songMapper, SingerServiceImpl singerService, ImageServiceImpl imageService, AudioServiceImpl audioService) {
        this.songMapper = songMapper;
        this.singerService = singerService;
        this.imageService = imageService;
        this.audioService = audioService;
    }

    /**
     * 根据 songIds 列表查询单曲
     */
    @Override
    public Object getSongsBySongIdList(List<Long> songIds) {
        IPage<Song> page = new Page<>(1, 1000);
        LambdaQueryWrapper<Song> lqw = new LambdaQueryWrapper<>();
        lqw.in(Song::getSongId, songIds).eq(Song::getDeleted, 0);
        songMapper.selectPage(page, lqw);
        // 没有数据
        if (page.getTotal() == 0) {
            return null;
        }
        return pageHandler(page).get("data");
    }

    /**
     * 按页查询所有信息并展示在表格中
     */
    @Override
    public Map<String, Object> getAllByPage(int currentPage, int pageSize) {
        IPage<Song> page = new Page<>(currentPage, pageSize);
        songMapper.selectPage(page, null);
        // 没有数据
        if (page.getTotal() == 0) {
            return null;
        }
        return pageHandler(page);
    }

    /**
     * 分页结果处理
     */
    @Override
    public Map<String, Object> pageHandler(IPage<Song> page) {
        // 返回结果 Map
        HashMap<String, Object> resultMap = new HashMap<>();
        // 分页数据集
        List<Song> records = page.getRecords();
        if (!records.isEmpty()) {
            // 图片索引集
            HashSet<Long> imgIndexes = new HashSet<>();
            // 将图片索引放入索引集
            records.forEach(item -> imgIndexes.add(item.getCover()));
            // 调用 ImageService 查询图片路径
            Map<Long, String> imageFilePaths = imageService.getAllFilenameByIdList(imgIndexes);

            // 音频索引集
            HashSet<Long> mp3Indexes = new HashSet<>();
            // 将音频索引放入索引集
            records.forEach(item -> mp3Indexes.add(item.getMp3()));
            // 调用 AudioService 查询音频路径
            Map<Long, String> audioFilePaths = audioService.getAllFilenameByIdList(mp3Indexes);

            // 单曲编号索引集
            HashSet<Long> songIndexes = new HashSet<>();
            // 将单曲编号放入索引集
            records.forEach(item -> songIndexes.add(item.getSongId()));
            // 调用 SongService 查询歌手编号
            List<HashMap<String, Long>> songIdSingerIdMapping = songMapper.selectSingerIdsBySongIdSet(songIndexes);
            // 歌手编号集合
            HashSet<Long> singerIds = new HashSet<>();
            for (Map<String, Long> item : songIdSingerIdMapping) {
                singerIds.add(item.get("singer"));
            }
            // 调用 SingerService 查询歌手名
            Map<Long, String> singerIdSingerNameMapping = singerService.getSingerNameBySongId(singerIds);
            // songId 与 singerName 的 mapping
            HashMap<Long, String> songIdSingerNameMapping = new HashMap<>();
            for (Map<String, Long> item : songIdSingerIdMapping) {
                songIdSingerNameMapping.put(item.get("song"), singerIdSingerNameMapping.get(item.get("singer")));
            }

            // 存储 Song 对象转 List 的结果集
            ArrayList<Map<String, Object>> recordList = new ArrayList<>();
            // 将 Song 对象转为 List 并格式化
            records.forEach(item -> {
                Map<String, Object> map = BeanUtil.beanToMap(item);
                map = songMapOutputToTableFormat(map, item, imageFilePaths, audioFilePaths, songIdSingerNameMapping);
                recordList.add(map);
            });

            resultMap.put("data", recordList); // 数据
            resultMap.put("pageSize", page.getSize()); // 每页显示数
            resultMap.put("currentPage", page.getCurrent()); // 当前页数
            resultMap.put("totalPage", page.getPages()); // 总页数
        }
        return resultMap;
    }

    /**
     * Song 的 Map 对象输出到表格的格式化
     */
    @Override
    public Map<String, Object> songMapOutputToTableFormat(Map<String, Object> map, Song song, Map<Long, String> imageFilePaths, Map<Long, String> audioFilePaths, HashMap<Long, String> songIdSingerNameMapping) {
        map.replace("songId", String.valueOf(map.get("songId")));

        // 图片有默认值，非空，更改图片索引为图片路径，更改键值为 image
        map.remove("cover");
        // 获取文件名
        String imageFilePath = imageFilePaths.get(song.getCover());
        // 对外网连接做特殊处理
        if (!imageFilePath.startsWith("http")) {
            imageFilePath = FileOpUtil.BASE_URL + "/image/download/" + imageFilePath;
        }
        // 更改键值为 image
        map.put("image", imageFilePath);

        // 更改音频索引为音频路径，更改键值为 audio
        map.remove("mp3");
        // 获取文件名
        String audioFilePath = audioFilePaths.get(song.getMp3());
        // 对外网连接做特殊处理
        if (!audioFilePath.startsWith("http")) {
            audioFilePath = FileOpUtil.BASE_URL + "/audio/download/" + audioFilePath;
        }
        // 更改键名为 audio
        map.put("audio", audioFilePath);

        // 为 singerName 赋值
        map.replace("singerName", songIdSingerNameMapping.get(Long.parseLong((String) map.get("songId"))));

        if (song.getInfo() == null) {
            map.replace("info", "");
        }
        if (song.getLyrics() == null) {
            map.replace("lyrics", "");
        }
        if (song.getDeleted() == 0) {
            map.replace("deleted", "正常");
        } else {
            map.replace("deleted", "注销");
        }
        return map;
    }

    /**
     * 按页模糊查询并展示在表格中
     */
    @Override
    public Map<String, Object> getAllBySearchPage(int currentPage, int pageSize, String songName, String lyrics) {
        IPage<Song> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Song> lqw = new LambdaQueryWrapper<>();
        if (songName != null && !songName.isEmpty()) {
            lqw.like(Song::getSongName, songName);
        }
        if (lyrics != null && !lyrics.isEmpty()) {
            lqw.like(Song::getLyrics, lyrics);
        }
        songMapper.selectPage(page, lqw);
        return pageHandler(page);
    }

    /**
     * 管理员新增单曲
     */
    @Override
    public int addSongByAdmin(Song song, String singerName) {
        Song format = songInsertFormat(song);
        if (format != null) {
            if (singerName != null && !singerName.isEmpty()) {
                // 查询歌手是否存在
                Long singerIds = singerService.selectSingerByName(singerName);
                if (singerIds != null) {
                    // 歌手存在则新增歌曲并记录歌手
                    songMapper.insert(format);
                    songMapper.addTbSingerSong(singerIds, format.getSongId());
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * 格式化新增 Song 对象
     */
    @Override
    public Song songInsertFormat(Song song) {
        // 获取属性名
        ArrayList<String> names = SongReflectUtil.getFieldNames();
        // 获取属性值
        HashMap<String, Object> values = SongReflectUtil.getFieldValues(song);
        // 获取方法
        HashMap<String, Method> methods = SongReflectUtil.getFieldMethods();
        for (String name : names) {
            if (values.get(name) == null || String.valueOf(values.get(name)).isEmpty()) {
                try {
                    switch (name) {
                        case "songName":
                        case "mp3":
                            return null;
                        case "cover":
                            // 设置默认单曲封面
                            methods.get(name).invoke(song, 1L);
                            break;
                        case "lyrics":
                        case "info":
                            methods.get(name).invoke(song, (Object) null);
                            break;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return song;
    }

    /**
     * 修改单曲数据
     */
    @Override
    public int modifySong(Song song) {
        LambdaUpdateWrapper<Song> wrapper = songModifyFormat(song);
        return songMapper.update(null, wrapper);
    }

    /**
     * 格式化修改的单曲数据
     */
    @Override
    public LambdaUpdateWrapper<Song> songModifyFormat(Song song) {
        // 获取属性名
        ArrayList<String> names = SongReflectUtil.getFieldNames();
        // 获取属性值
        HashMap<String, Object> values = SongReflectUtil.getFieldValues(song);
        LambdaUpdateWrapper<Song> wrapper = new LambdaUpdateWrapper<>();
        // 记录 songId
        Long songId = null;
        for (String name : names) {
            switch (name) {
                case "songId":
                    wrapper.eq(Song::getSongId, values.get(name));
                    songId = (Long) values.get(name);
                    break;
                case "songName":
                    if (!String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(Song::getSongName, values.get(name));
                    }
                    break;
                case "singerName":
                    if (!String.valueOf(values.get(name)).isEmpty()) {
                        Long isExist = singerService.selectSingerByName(String.valueOf(values.get(name)));
                        if (isExist != null) {
                            songMapper.updateSinger(songId, isExist);
                        }
                    }
                    break;
                case "cover":
                    if (values.get(name) != null && !String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(Song::getCover, values.get(name));
                    }
                    break;
                case "mp3":
                    if (values.get(name) != null && !String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(Song::getMp3, values.get(name));
                    }
                    break;
                case "info":
                    if (String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(Song::getInfo, null);
                    } else {
                        wrapper.set(Song::getInfo, values.get(name));
                    }
                    break;
                case "lyrics":
                    if (String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(Song::getLyrics, null);
                    } else {
                        wrapper.set(Song::getLyrics, values.get(name));
                    }
                    break;
            }
        }
        return wrapper;
    }

    /**
     * 更新单曲状态
     */
    @Override
    public int toggleState(Long id, int state) {
        LambdaUpdateWrapper<Song> wrapper = new LambdaUpdateWrapper<>();
        if (state == 0) {
            wrapper.eq(Song::getSongId, id).set(Song::getDeleted, 1);
        } else {
            wrapper.eq(Song::getSongId, id).set(Song::getDeleted, 0);
        }
        return songMapper.update(null, wrapper);
    }

    /**
     * 获取单个单曲数据
     */
    @Override
    public Object getOneSongInfo(Long songId) {
        ArrayList<Long> oneSongList = new ArrayList<>();
        oneSongList.add(songId);
        return getSongsBySongIdList(oneSongList);
    }
}
