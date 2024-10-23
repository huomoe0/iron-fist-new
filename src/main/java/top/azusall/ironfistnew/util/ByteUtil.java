package top.azusall.ironfistnew.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import top.azusall.ironfistnew.entity.IronFistPlayer;

/**
 * @Author: liumingda
 * @Date: 2024/10/23 23:46
 * @Description:
 */
@Slf4j
public class ByteUtil {


    public static byte[] encoding(IronFistPlayer ironFistPlayer) {
        String json = new Gson().toJson(ironFistPlayer);
        return json.getBytes();
    }

    public static IronFistPlayer decoding(byte[] bytes) {
        return new Gson().fromJson(new String(bytes), IronFistPlayer.class);
    }
}
