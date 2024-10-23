package top.azusall.ironfistnew;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import top.azusall.ironfistnew.command.CommandIronFist;
import top.azusall.ironfistnew.common.StateSaverAndLoader;
import top.azusall.ironfistnew.entity.IronFistPlayer;
import top.azusall.ironfistnew.entity.S2CInitSyncPayload;
import top.azusall.ironfistnew.entity.S2CSyncPayload;
import top.azusall.ironfistnew.service.BlockBreakService;
import top.azusall.ironfistnew.util.ByteUtil;


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


    private final BlockBreakService blockBreakService = new BlockBreakService();

    @Override
    public void onInitialize() {

        // 加入服务器时同步
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            IronFistPlayer playerState = StateSaverAndLoader.getPlayerState(handler.getPlayer());

            S2CInitSyncPayload s2CSyncPayload = new S2CInitSyncPayload(ByteUtil.encoding(playerState));
            // 向客户端发送数据包

            ServerPlayerEntity playerEntity = handler.player;
            server.execute(() -> {
                ServerPlayNetworking.send(playerEntity, s2CSyncPayload);
            });
        });

        // 注册命令 fist
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("fist")
                    .then(CommandManager.literal("addxp").executes(CommandIronFist::addXp))
                    .then(CommandManager.literal("levelup").executes(CommandIronFist::levelUp))
                    .then(CommandManager.literal("showxp").executes(CommandIronFist::showXp))
                    .then(CommandManager.literal("showlevel").executes(CommandIronFist::showLevel))
                    .requires(source -> source.hasPermissionLevel(1))
                    .executes(CommandIronFist::getCommandUsage));
        });


        // 注册一个方块挖掘事件监听器
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if (state.getBlock() == Blocks.GRASS_BLOCK || state.getBlock() == Blocks.DIRT) {
                IronFistPlayer playerState = StateSaverAndLoader.getPlayerState(player);
                // 空手,当泥土方块被挖掘时增加计数
                if (player.getMainHandStack().isEmpty()) {
                    blockBreakService.onBlockBreak(player, world, pos, state, playerState);
                } else {
                    return;
                }
                log.info("playerXP: {}, level: {}, fatigue:{}, cumulativework: {}, lastBreakMillis:{}, nextNeedXp: {}",
                        playerState.getFistXp(), playerState.getFistLevel(), playerState.getEnergy(), playerState.getCumulativeWork(),
                        playerState.getLastBreakMillis(), blockBreakService.getLevelUpXp(playerState.getFistLevel()));


                MinecraftServer server = world.getServer();
                S2CSyncPayload s2CSyncPayload = new S2CSyncPayload(ByteUtil.encoding(playerState));
                // 向客户端发送数据包
                ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(player.getUuid());
                server.execute(() -> {
                    ServerPlayNetworking.send(playerEntity, s2CSyncPayload);

                });
            }
        });


        LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
            // 我们只修改内置战利品表，而不通过检查源代码来修改数据包战利品表。
            // 我们还要检查战利品表 ID 是否等于我们想要的 ID。
            if (source.isBuiltin() && key.equals(Blocks.GRASS_BLOCK.getLootTableKey())) {
                // We make the pool and add an item
                LootPool.Builder poolBuilder = LootPool.builder().with(ItemEntry.builder(Items.EGG));
                tableBuilder.pool(poolBuilder);
            }
        });

    }
}
