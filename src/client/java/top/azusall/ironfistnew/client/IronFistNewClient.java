package top.azusall.ironfistnew.client;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import top.azusall.ironfistnew.IronFistNew;
import top.azusall.ironfistnew.entity.IronFistPlayer;
import top.azusall.ironfistnew.entity.S2CInitSyncPayload;
import top.azusall.ironfistnew.entity.S2CSyncPayload;
import top.azusall.ironfistnew.util.ByteUtil;

/**
 * @author houmoe
 */
@Slf4j
public class IronFistNewClient implements ClientModInitializer {

    private static IronFistPlayer ironFistPlayer = new IronFistPlayer();


    @Override
    public void onInitializeClient() {
// 注册payload
        PayloadTypeRegistry.playS2C().register(S2CSyncPayload.ID, S2CSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(S2CInitSyncPayload.ID, S2CInitSyncPayload.CODEC);

        // 进入世界加载
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            log.info("--------------------ClientPlayConnectionEvents.INIT---------------");


        });

        ClientPlayNetworking.registerGlobalReceiver(IronFistNew.INITIAL_SYNC_ID, (payload, context) -> {
            S2CInitSyncPayload payload1 = (S2CInitSyncPayload) payload;
            ironFistPlayer = ByteUtil.decoding(payload1.value());

            context.client().execute(() -> {
                context.player().sendMessage(Text.literal("初始化的当前玩家fist：" + new Gson().toJson(ironFistPlayer)));
            });
        });


        ClientPlayNetworking.registerGlobalReceiver(IronFistNew.IRONFISTNEW_ID, (payload, context) -> {
            context.client().execute(() -> {
                // 拿到服务端发送的数据包
                ClientPlayerEntity player = context.player();
                S2CSyncPayload payload1 = (S2CSyncPayload) payload;
                ironFistPlayer = ByteUtil.decoding(payload1.value());
//                player.sendMessage(Text.literal(player.getUuid().toString()));
                player.sendMessage(Text.literal(ironFistPlayer.getFistLevel() + " " + ironFistPlayer.getFistXp() + " " +
                        ironFistPlayer.getEnergy() + " " + ironFistPlayer.getCumulativeWork() + " " + ironFistPlayer.getLastBreakMillis()));
            });
        });


        // 破坏方块事件
        ClientPlayerBlockBreakEvents.AFTER.register((world, player, pos, state) -> {

//            player.sendMessage(Text.literal("ClientPlayerBlockBreakEvents"));
        });


    }
}
