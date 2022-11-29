package music.website_backend.service.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.List;

public class FileOpUtil {
    // 访问路径前缀
    public static String BASE_URL;

    static {
        try {
            BASE_URL = "http://" + InetAddress.getLocalHost().getHostAddress() + ":8080/api";
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    // 文件保存地址前缀
    public static final String BASE_PATH = System.getProperty("user.dir") + File.separator +
            "website_backend" + File.separator + "src" + File.separator + "main" + File.separator +
            "resources" + File.separator + "files" + File.separator;

    public static String writeImg(MultipartFile file) {
        if (!file.isEmpty()) {
            // 获取文件名
            String filename = file.getOriginalFilename();
            // 文件标识
            String uuid = IdUtil.fastSimpleUUID();
            // 文件名
            String fullName = uuid + "_" + filename;
            // 写入文件
            try {
                FileUtil.writeBytes(file.getBytes(), BASE_PATH + "images" + File.separator + fullName);
                return fullName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void readImg(String filename, HttpServletResponse response) {
        // 获取所有文件名称
        List<String> names = FileUtil.listFileNames(BASE_PATH + "images" + File.separator);
        // 找到文件名一致的文件
        String file = names.stream().filter(name -> name.contains(filename)).findAny().orElse("");
        if (StrUtil.isNotEmpty(file)) {
            try {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("image/jpeg");
            byte[] bytes = FileUtil.readBytes(BASE_PATH + "images" + File.separator + file);
            try {
                // 输出流
                OutputStream os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readMusic(String filename, HttpServletResponse response) {
        // 获取所有文件名称
        List<String> names = FileUtil.listFileNames(BASE_PATH + "music" + File.separator);
        // 找到文件名一致的文件
        String file = names.stream().filter(name -> name.contains(filename)).findAny().orElse("");
        if (StrUtil.isNotEmpty(file)) {
            response.addHeader("Accept-Ranges", "bytes");
            response.setContentType("audio/mpeg");
            byte[] bytes = FileUtil.readBytes(BASE_PATH + "music" + File.separator + file);
            try {
                // 输出流
                OutputStream os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String writeMusic(MultipartFile file) {
        if (!file.isEmpty()) {
            // 获取文件名
            String filename = file.getOriginalFilename();
            // 文件标识
            String uuid = IdUtil.fastSimpleUUID();
            // 文件名
            String fullName = uuid + "_" + filename;
            // 写入文件
            try {
                FileUtil.writeBytes(file.getBytes(), BASE_PATH + "music" + File.separator + fullName);
                return fullName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean removeImg(String fileName) {
        return FileUtil.del(BASE_PATH + "images" + File.separator + fileName);
    }

    public static boolean removeMusic(String fileName) {
        return FileUtil.del(BASE_PATH + "music" + File.separator + fileName);
    }
}
