package music.website_backend.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import music.website_backend.service.utils.FileOpUtil;
import music.website_backend.entity.Audio;
import music.website_backend.mapper.AudioMapper;
import music.website_backend.service.AudioService;
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
public class AudioServiceImpl extends ServiceImpl<AudioMapper, Audio> implements AudioService {
    private final AudioMapper audioMapper;

    public AudioServiceImpl(AudioMapper audioMapper) {
        this.audioMapper = audioMapper;
    }

    /**
     * 根据传入的 id 列表查询数据库音频名字段
     */
    @Override
    public Map<Long, String> getAllFilenameByIdList(Set<Long> indexes) {
        LambdaQueryWrapper<Audio> lqw = new LambdaQueryWrapper<>();
        // 根据传入的主键 id 查询 id、filename
        lqw.select(Audio::getAudioId, Audio::getFilename).in(Audio::getAudioId, indexes);
        // 查询
        List<Audio> audioList = audioMapper.selectList(lqw);
        HashMap<Long, String> filenameMap = new HashMap<>();
        // 抽取 filename 为 Map
        audioList.forEach(item -> filenameMap.put(item.getAudioId(), item.getFilename()));
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
        LambdaQueryWrapper<Audio> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Audio::getMd5, md5Hex);
        Audio audio = audioMapper.selectOne(lqw);
        if (audio == null) {
            String filename = FileOpUtil.writeMusic(file);
            Audio newAudio = new Audio(filename, md5Hex);
            int insert = audioMapper.insert(newAudio);
            if (insert == 1) {
                return newAudio.getAudioId();
            }
            return 0L;
        } else {
            return audio.getAudioId();
        }
    }

    /**
     * 下载
     */
    @Override
    public void download(String filename, HttpServletResponse response) {
        if (filename != null && !filename.isEmpty()) {
            FileOpUtil.readMusic(filename, response);
        }
    }
}
