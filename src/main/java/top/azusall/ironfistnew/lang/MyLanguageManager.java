package top.azusall.ironfistnew.lang;

import lombok.extern.slf4j.Slf4j;
import top.azusall.ironfistnew.util.FileUtil;

import java.util.HashMap;


/**
 * @author houmo
 */
@Slf4j
public class MyLanguageManager {

    private static HashMap<String, String> langMap = new HashMap<>();


    private MyLanguageManager() {
    }


    public static void init(String language) {
        log.info("MyLanguageManager Loading language {}", language);
        loadLanguage(language);
    }


    private static void loadLanguage(String language) {
        String path = "assets/ironfistnew/lang/" + language + ".json";

        log.info("load language {}", path);
        langMap = FileUtil.readResourceJsonFile(path, HashMap.class);
        if (langMap == null) {
            log.info("load language {}", "assets/ironfistnew/lang/en_us.json");
            langMap = FileUtil.readResourceJsonFile("assets/ironfistnew/lang/en_us.json", HashMap.class);
        }

    }


    public static String getText(String key, Object... args) {
        String s = langMap.get(key);
        return String.format(s, args);
    }
}
