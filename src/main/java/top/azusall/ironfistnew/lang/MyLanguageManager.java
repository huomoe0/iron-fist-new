package top.azusall.ironfistnew.lang;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.text.Text;

import java.io.*;
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
        try {
            String path = "assets/ironfistnew/lang/" + language + ".json";

            InputStream resourceAsStream = MyLanguageManager.class.getClassLoader().getResourceAsStream(path);
            if (resourceAsStream == null) {
                resourceAsStream = MyLanguageManager.class.getClassLoader().getResourceAsStream("assets/ironfistnew/lang/en_us.json");
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
            langMap = new Gson().fromJson(bufferedReader, HashMap.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    public static Text getText(String key, Object... args) {
        String s = langMap.get(key);
        return Text.literal(String.format(s, args));
    }
}
