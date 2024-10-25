package top.azusall.ironfistnew.client.util;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import top.azusall.ironfistnew.entity.IronFistPlayer;
import top.azusall.ironfistnew.entity.MyPayloadBase;
import top.azusall.ironfistnew.util.PayloadUtil;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author: liumingda
 * @Date: 2024/10/25 07:43
 * @Description:
 */
@Slf4j
public class ClientPayloadUtil {

    /**
     * 向客户端发送同步数据包
     */
    public static <T extends MyPayloadBase> void sendToClient(IronFistPlayer ironFistPlayer, Class<T> clazz) {
        try {
            T myPayloadBase = clazz.getConstructor(byte[].class).newInstance(PayloadUtil.encoding(ironFistPlayer));
            ClientPlayNetworking.send(myPayloadBase);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
    }
}
