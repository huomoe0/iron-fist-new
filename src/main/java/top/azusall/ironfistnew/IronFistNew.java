package top.azusall.ironfistnew;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.*;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import top.azusall.ironfistnew.entity.IronFistPlayer;
import top.azusall.ironfistnew.entity.MyC2SSyncPayload;
import top.azusall.ironfistnew.entity.MyS2CInitPayload;
import top.azusall.ironfistnew.entity.MyS2CSyncPayload;
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
    public static final Identifier INITIAL_SYNC = Identifier.of(MOD_ID, "initial_sync");
    public static final CustomPayload.Id IRONFISTNEW_ID = new CustomPayload.Id(IRONFISTNEW);
    public static final CustomPayload.Id INITIAL_SYNC_ID = new CustomPayload.Id(INITIAL_SYNC);

    @Override
    public void onInitialize() {
        registerPayloadReceiver();
        registerPayloadSender();
        registerBlockBreakEvents();
    }

    private void registerPayloadReceiver() {
        PayloadTypeRegistry.playC2S().register(MyC2SSyncPayload.ID, MyC2SSyncPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(MyC2SSyncPayload.ID, ((payload, context) -> {
            IronFistPlayer ironFistPlayer = PayloadUtil.decodePayload(payload);
            ServerPlayerEntity player = context.player();
            StateSaverAndLoader.setPlayerState(player, ironFistPlayer);
        }));
    }

    /**
     * 与客户端发送数据
     */
    private void registerPayloadSender() {
        // 加入服务器时同步
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            IronFistPlayer playerState = StateSaverAndLoader.getPlayerState(handler.getPlayer());
            PayloadUtil.sendToClient(handler.player, playerState, MyS2CInitPayload.class);
        });
    }

    /**
     * 注册方块破坏处理
     */
    private void registerBlockBreakEvents() {
        BlockBreakService blockBreakService = BlockBreakService.INSTANCE;

        // 注册一个方块挖掘事件监听器
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            // 只有空手或者非工具挖掘记录
            if (isTool(player.getMainHandStack().getItem())) {
                return;
            }
            IronFistPlayer playerState = StateSaverAndLoader.getPlayerState(player);

            blockBreakService.onBlockBreak(player, world, pos, state, playerState);
            PayloadUtil.sendToClient((ServerPlayerEntity) player, playerState, MyS2CSyncPayload.class);
        });
    }


    public static boolean isTool(Item item) {
        if (item instanceof ToolItem) {
                return true;
            }
        return false;
    }
}
