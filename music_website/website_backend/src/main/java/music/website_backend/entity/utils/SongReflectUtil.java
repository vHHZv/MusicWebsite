package music.website_backend.entity.utils;

import music.website_backend.entity.Song;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class SongReflectUtil {
    /**
     * 获取属性
     */
    public static Field[] getFields() {
        // 获取 class 类
        Class<Song> songClass = Song.class;
        // 获取属性
        return songClass.getDeclaredFields();
    }

    /**
     * 获取属性名
     */
    public static ArrayList<String> getFieldNames() {
        Field[] fields = getFields();
        // 属性名列表
        ArrayList<String> fieldNames = new ArrayList<>();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    /**
     * 获取属性对应的值
     */
    public static HashMap<String, Object> getFieldValues(Song song) {
        ArrayList<String> fieldNames = getFieldNames();
        HashMap<String, Object> map = new HashMap<>();
        for (String fieldName : fieldNames) {
            try {
                // 根据属性名获取 get 方法并执行
                PropertyDescriptor descriptor = new PropertyDescriptor(fieldName, Song.class);
                Object value = descriptor.getReadMethod().invoke(song);
                map.put(fieldName, value);
            } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
                System.out.println("Song反射readMethod异常");
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 获取属性对应的方法
     */
    public static HashMap<String, Method> getFieldMethods() {
        ArrayList<String> fieldNames = getFieldNames();
        HashMap<String, Method> map = new HashMap<>();
        for (String fieldName : fieldNames) {
            try {
                // 根据属性名获取 set 方法
                Method method = new PropertyDescriptor(fieldName, Song.class).getWriteMethod();
                map.put(fieldName, method);
            } catch (IntrospectionException e) {
                System.out.println("Song反射writeMethod异常");
                e.printStackTrace();
            }
        }
        return map;
    }
}
