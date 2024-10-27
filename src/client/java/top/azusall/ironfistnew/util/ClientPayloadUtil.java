package top.azusall.ironfistnew.util;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import top.azusall.ironfistnew.entity.IronFistPlayer;
import top.azusall.ironfistnew.entity.SyncPacket;

/**
 * @author huomoe
 */
@Slf4j
public class ClientPayloadUtil {


    /**
     * 客户端本地发送同步数据包
     */
    public static void sendToClient(IronFistPlayer ironFistPlayer) {
        PacketByteBuf packetByteBuf = PacketByteBufs.create();
        packetByteBuf.writeString(PayloadUtil.encoding(ironFistPlayer));
        ClientPlayNetworking.send(new SyncPacket(packetByteBuf));
    }
}
