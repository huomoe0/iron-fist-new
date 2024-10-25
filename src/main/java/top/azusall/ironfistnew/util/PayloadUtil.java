package top.azusall.ironfistnew.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import top.azusall.ironfistnew.entity.IronFistPlayer;
import top.azusall.ironfistnew.entity.MyPayloadBase;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author: liumingda
 * @Date: 2024/10/23 23:46
 * @Description:
 */
@Slf4j
public class PayloadUtil {


    public static byte[] encoding(IronFistPlayer ironFistPlayer) {
        String json = new Gson().toJson(ironFistPlayer);
        return json.getBytes();
    }

    /**
     * 向客户端发送同步数据包
     */
    public static <T extends MyPayloadBase> void sendToClient(ServerPlayerEntity player, IronFistPlayer ironFistPlayer, Class<T> clazz) {
        try {
            T myPayloadBase = clazz.getConstructor(byte[].class).newInstance(encoding(ironFistPlayer));
            MinecraftServer server = player.getServer();
            server.execute(() -> {
                ServerPlayNetworking.send(player, myPayloadBase);
            });
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
    }


    public static IronFistPlayer decodePayload(MyPayloadBase payload) {
        byte[] value = payload.getValue();
        return new Gson().fromJson(new String(value), IronFistPlayer.class);
    }
}
