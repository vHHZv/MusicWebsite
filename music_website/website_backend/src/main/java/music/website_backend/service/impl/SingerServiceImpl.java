package music.website_backend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import music.website_backend.service.utils.FileOpUtil;
import music.website_backend.entity.Singer;
import music.website_backend.entity.utils.SingerReflectUtil;
import music.website_backend.mapper.SingerMapper;
import music.website_backend.service.SingerService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Service
public class SingerServiceImpl extends ServiceImpl<SingerMapper, Singer> implements SingerService {
    private final SongServiceImpl songService;
    private final SingerMapper singerMapper;
    private final ImageServiceImpl imageService;

    public SingerServiceImpl(@Lazy SongServiceImpl songService, SingerMapper singerMapper, ImageServiceImpl imageService) {
        this.songService = songService;
        this.singerMapper = singerMapper;
        this.imageService = imageService;
    }

    /**
     * 根据姓名查找歌手
     */
    @Override
    public Long selectSingerByName(String name) {
        LambdaQueryWrapper<Singer> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Singer::getSingerName, name);
        List<Singer> singers = singerMapper.selectList(lqw);
        if (!singers.isEmpty()) {
            return singers.get(0).getSid();
        }
        return null;
    }

    /**
     * 按页查询所有信息并展示在表格中
     */
    @Override
    public Map<String, Object> getAllByPage(int currentPage, int pageSize) {
        IPage<Singer> page = new Page<>(currentPage, pageSize);
        singerMapper.selectPage(page, null);
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
    public Map<String, Object> pageHandler(IPage<Singer> page) {
        // 返回结果 Map
        HashMap<String, Object> resultMap = new HashMap<>();
        // 分页数据集
        List<Singer> records = page.getRecords();
        if (!records.isEmpty()) {
            // 图片索引集
            HashSet<Long> imgIndexes = new HashSet<>();
            // 将图片索引放入索引集
            records.forEach(item -> imgIndexes.add(item.getAvatar()));
            // 调用 ImageService 查询图片路径
            Map<Long, String> imageFilePaths = imageService.getAllFilenameByIdList(imgIndexes);

            // 存储 Singer 对象转 List 的结果集
            ArrayList<Map<String, Object>> recordList = new ArrayList<>();
            // 将 Singer 对象转为 List 并格式化
            records.forEach(item -> {
                Map<String, Object> map = BeanUtil.beanToMap(item);
                map = singerMapOutputToTableFormat(map, item, imageFilePaths);
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
     * Singer 的 Map 对象输出到表格的格式化
     */
    @Override
    public Map<String, Object> singerMapOutputToTableFormat(Map<String, Object> map, Singer singer, Map<Long, String> imageFilePaths) {
        map.replace("sid", String.valueOf(map.get("sid")));

        // 图片有默认值，非空，更改图片索引为图片路径，更改键值为 image
        map.remove("avatar");
        // 获取文件名
        String imageFilePath = imageFilePaths.get(singer.getAvatar());
        // 对外网连接做特殊处理
        if (!imageFilePath.startsWith("http")) {
            imageFilePath = FileOpUtil.BASE_URL + "/image/download/" + imageFilePath;
        }
        // 更改键值为 image
        map.put("image", imageFilePath);

        if (singer.getInfo() == null) {
            map.replace("info", "");
        }
        if (singer.getDeleted() == 0) {
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
    public Map<String, Object> getAllBySearchPage(int currentPage, int pageSize, String singerName, String alias) {
        IPage<Singer> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Singer> lqw = new LambdaQueryWrapper<>();
        if (singerName != null && !singerName.isEmpty()) {
            lqw.like(Singer::getSingerName, singerName);
        }
        if (alias != null && !alias.isEmpty()) {
            lqw.like(Singer::getAlias, alias);
        }
        singerMapper.selectPage(page, lqw);
        return pageHandler(page);
    }

    /**
     * 管理员新增歌手
     */
    @Override
    public int addSingerByAdmin(Singer singer) {
        Singer format = singerInsertFormat(singer);
        if (format != null) {
            return singerMapper.insert(format);
        }
        return 0;
    }

    /**
     * 格式化新增 Singer 对象
     */
    @Override
    public Singer singerInsertFormat(Singer singer) {
        // 获取属性名
        ArrayList<String> names = SingerReflectUtil.getFieldNames();
        // 获取属性值
        HashMap<String, Object> values = SingerReflectUtil.getFieldValues(singer);
        // 获取方法
        HashMap<String, Method> methods = SingerReflectUtil.getFieldMethods();
        for (String name : names) {
            if (values.get(name) == null || String.valueOf(values.get(name)).isEmpty())
                try {
                    switch (name) {
                        case "singerName":
                            return null;
                        case "avatar":
                            // 设置默认歌手头像
                            methods.get(name).invoke(singer, 1L);
                            break;
                        case "alias":
                        case "info":
                            methods.get(name).invoke(singer, (Object) null);
                            break;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
        }
        return singer;
    }

    /**
     * 修改歌手数据
     */
    @Override
    public int modifySinger(Singer singer) {
        LambdaUpdateWrapper<Singer> wrapper = singerModifyFormat(singer);
        return singerMapper.update(null, wrapper);
    }

    /**
     * 格式化修改的歌手数据
     */
    @Override
    public LambdaUpdateWrapper<Singer> singerModifyFormat(Singer singer) {
        // 获取属性名
        ArrayList<String> names = SingerReflectUtil.getFieldNames();
        // 获取属性值
        HashMap<String, Object> values = SingerReflectUtil.getFieldValues(singer);
        LambdaUpdateWrapper<Singer> wrapper = new LambdaUpdateWrapper<>();
        for (String name : names) {
            switch (name) {
                case "sid":
                    wrapper.eq(Singer::getSid, values.get(name));
                    break;
                case "singerName":
                    if (!String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(Singer::getSingerName, values.get(name));
                    }
                    break;
                case "alias":
                    if (String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(Singer::getAlias, null);
                    } else {
                        wrapper.set(Singer::getAlias, values.get(name));
                    }
                    break;
                case "avatar":
                    if (values.get(name) != null && !String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(Singer::getAvatar, values.get(name));
                    }
                    break;
                case "info":
                    if (String.valueOf(values.get(name)).isEmpty()) {
                        wrapper.set(Singer::getInfo, null);
                    } else {
                        wrapper.set(Singer::getInfo, values.get(name));
                    }
                    break;
            }
        }
        return wrapper;
    }

    /**
     * 更新歌手状态
     */
    @Override
    public int toggleState(Long id, int state) {
        LambdaUpdateWrapper<Singer> wrapper = new LambdaUpdateWrapper<>();
        if (state == 0) {
            wrapper.eq(Singer::getSid, id).set(Singer::getDeleted, 1);
        } else {
            wrapper.eq(Singer::getSid, id).set(Singer::getDeleted, 0);
        }
        return singerMapper.update(null, wrapper);
    }

    /**
     * 获取歌手所有信息
     */
    @Override
    public Map<String, Object> getOneSingerInfo(Long sid) {
        Object baseInfo = getOneSingerBaseInfo(sid);
        if (baseInfo != null) {
            Object songs = getSongsBySingerId(sid);
            HashMap<String, Object> map = new HashMap<>();
            map.put("baseInfo", baseInfo);
            map.put("songs", songs);
            return map;
        }
        return null;
    }

    /**
     * 获取单个歌手基础数据
     */
    @Override
    public Object getOneSingerBaseInfo(Long sid) {
        IPage<Singer> page = new Page<>(1, 1);
        LambdaQueryWrapper<Singer> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Singer::getSid, sid).eq(Singer::getDeleted, 0);
        singerMapper.selectPage(page, lqw);
        // 没有数据
        if (page.getTotal() == 0) {
            return null;
        }
        return ((List<?>) pageHandler(page).get("data")).get(0);
    }

    /**
     * 获取歌手的单曲信息
     */
    @Override
    public Object getSongsBySingerId(Long sid) {
        // 获取歌手的歌曲 id
        List<Long> songIds = singerMapper.selectSongIdsBySingerId(sid);
        if (!songIds.isEmpty()) {
            return songService.getSongsBySongIdList(songIds);
        }
        return null;
    }

    /**
     * 根据传入的 id 查询歌手名
     */
    @Override
    public Map<Long, String> getSingerNameBySongId(Set<Long> indexes) {
        LambdaQueryWrapper<Singer> lqw = new LambdaQueryWrapper<>();
        // 根据传入的主键 id 查询 sid、SingerName
        lqw.select(Singer::getSid, Singer::getSingerName).in(Singer::getSid, indexes);
        // 查询
        List<Singer> singerList = singerMapper.selectList(lqw);
        HashMap<Long, String> singerNameMap = new HashMap<>();
        // 抽取 singerNameMap 为 Map
        singerList.forEach(item -> singerNameMap.put(item.getSid(), item.getSingerName()));
        return singerNameMap;
    }
}
