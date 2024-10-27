package top.azusall.ironfistnew.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author huomoe
 */
@Slf4j
public class FileUtil {

    /**
     * 读取resource下的文件, 不保留注释
     *
     * @param path  资源文件路径
     * @param clazz 类型
     * @param <T>   序列化类型
     * @return clazz对应的实体类
     */
    public static <T> T readResourceYmlFile(String path, Class<T> clazz) {
        T t = null;
        try {
            InputStream resourceAsStream = FileUtil.class.getClassLoader().getResourceAsStream(path);
            Yaml yaml = new Yaml();
            t = yaml.loadAs(resourceAsStream, clazz);
            resourceAsStream.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return t;
    }


    public static <T> T readConfigFile(String path, Class<T> clazz) {
        T data = null;
        try (InputStream inputStream = new FileInputStream(path)) {
            Yaml yaml = new Yaml();
            data = yaml.loadAs(inputStream, clazz);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return data;
    }

    /**
     * 写入 YAML 文件
     *
     * @param data
     * @param path
     * @param <T>
     */
    public static <T> void writeConfigFile(T data, String path) {
        try (Writer writer = new FileWriter(path)) {
            Yaml yaml = new Yaml();
            yaml.dump(data, writer);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    public static void copyFile(String classpathPath, String destPath) {
        try (InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(classpathPath)) {
            File file = new File(destPath);
            PrintWriter printWriter = new PrintWriter(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            bufferedReader.lines().forEach(line -> {
                printWriter.println(line);
            });
            printWriter.close();
            bufferedReader.close();
            log.info("File copied successfully with comments preserved from classpath to {}", destPath);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}