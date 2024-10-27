package top.azusall.ironfistnew.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import top.azusall.ironfistnew.entity.IronFistPlayer;
import top.azusall.ironfistnew.entity.SyncPacket;

/**
 * @Author: huomoe0
 * @Date: 2024/10/23 23:46
 * @Description:
 */
@Slf4j
public class PayloadUtil {

    public static String encoding(IronFistPlayer ironFistPlayer) {
        return new Gson().toJson(ironFistPlayer);
    }

    /**
     * 向客户端发送同步数据包
     */
    public static void sendToClient(ServerPlayerEntity player, IronFistPlayer ironFistPlayer) {

        PacketByteBuf packetByteBuf = PacketByteBufs.create();
        packetByteBuf.writeString(encoding(ironFistPlayer));

        MinecraftServer server = player.getServer();
        server.execute(() -> {
            ServerPlayNetworking.send(player, new SyncPacket(packetByteBuf));
        });
    }

    public static IronFistPlayer decodePayload(PacketByteBuf packetByteBuf) {
        String s = packetByteBuf.readString();
        return new Gson().fromJson(s, IronFistPlayer.class);
    }
}
