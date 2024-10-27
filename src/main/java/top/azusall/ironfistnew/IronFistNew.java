package top.azusall.ironfistnew;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import top.azusall.ironfistnew.entity.IronFistPlayer;
import top.azusall.ironfistnew.service.BlockBreakService;
import top.azusall.ironfistnew.service.StateSaverAndLoader;
import top.azusall.ironfistnew.util.PayloadUtil;


/**
 * @author houmo
 */
@Slf4j
public class IronFistNew implements ModInitializer {

    public static final String MOD_ID = "ironfistnew";
    public static final Identifier IRONFISTNEW = Identifier.of(MOD_ID, "ironfistnew1");

    @Override
    public void onInitialize() {
        registerPayloadReceiver();
        registerPayloadSender();
        registerBlockBreakEvents();
    }

    /**
     * 注册接收payload
     */
    private void registerPayloadReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(IRONFISTNEW, (MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf payload, PacketSender sender) -> {
            IronFistPlayer ironFistPlayer = PayloadUtil.decodePayload(payload);
            StateSaverAndLoader.setPlayerState(player, ironFistPlayer);
        });
    }

    /**
     * 与客户端发送数据
     */
    private void registerPayloadSender() {
        // 加入服务器时同步
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            IronFistPlayer playerState = StateSaverAndLoader.getPlayerState(handler.getPlayer());
            PayloadUtil.sendToClient(handler.player, playerState);
        });
    }

    /**
     * 注册方块破坏处理
     */
    private void registerBlockBreakEvents() {
        // 注册一个方块挖掘事件监听器
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            // 只有空手或者非工具挖掘记录
            if (!BlockBreakService.canExecute(player)) {
                return;
            }
            IronFistPlayer playerState = StateSaverAndLoader.getPlayerState(player);

            BlockBreakService.onBlockBreak(player, world, pos, state, playerState);
            PayloadUtil.sendToClient((ServerPlayerEntity) player, playerState);
        });
    }



}
