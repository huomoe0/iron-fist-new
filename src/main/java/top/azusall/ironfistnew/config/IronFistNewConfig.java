package top.azusall.ironfistnew.config;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.azusall.ironfistnew.service.BlockBreakService;
import top.azusall.ironfistnew.util.FileUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author houmo
 */
@Slf4j
@Data
public class IronFistNewConfig {

    private static  IronFistConfigModel instance;

    public static void loadConfig(String path) {
        String configPathStr = path + "/ironfistnew.yml";
        log.info("load config {}", configPathStr);
        Path configPath = Paths.get(configPathStr);
        if (!Files.exists(configPath)) {
            log.info("配置文件不存在，生成配置");
            generateDefaultConfig(configPathStr);
        }
        instance = FileUtil.readConfigFile(configPathStr, IronFistConfigModel.class);
        BlockBreakService.loadLevelList(instance.getLevel());
        log.info("now config: {}", new Gson().toJson(instance));
    }

    /**
     * 生成默认配置文件
     *
     * @param pathTo 要生成的文件位置
     */
    private static void generateDefaultConfig(String pathTo) {
        FileUtil.copyFile("assets/ironfistnew/config/ironfistnew-default.yml", pathTo);
    }


    public static int getEnergyRecoveryFactor() {
        return instance.getEnergyRecoveryFactor();
    }

    public static int getSpeedMultiple() {
        return instance.getSpeedMultiple();
    }


    public static int getMillisecondsHardnessOne() {
        return instance.getMillisecondsHardnessOne();
    }


    public static float getEnergyThreshold() {
        return instance.getEnergyThreshold();
    }


    public static float getDamageAmount() {
        return instance.getDamageAmount();
    }


    public static float getMinHealth() {
        return instance.getMinHealth();
    }

    public static HashMap<Integer, ArrayList<String>> getLevel() {
        return instance.getLevel();
    }

}
