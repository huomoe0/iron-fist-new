package top.azusall.ironfistnew;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import top.azusall.ironfistnew.command.IronFistCommand;
import top.azusall.ironfistnew.entity.IronFistPlayer;
import top.azusall.ironfistnew.entity.MyS2CInitPayload;
import top.azusall.ironfistnew.entity.MyS2CSyncPayload;
import top.azusall.ironfistnew.lang.MyLanguageManager;
import top.azusall.ironfistnew.service.BlockBreakService;
import top.azusall.ironfistnew.util.PayloadUtil;

/**
 * @author houmoe
 */
@Slf4j
public class IronFistNewClient implements ClientModInitializer {

    public static IronFistPlayer ironFistPlayer = new IronFistPlayer();

    @Override
    public void onInitializeClient() {
        registerLanguageManager();
        registerCommands();
        registerGlobalReceiver();
        registerBlockBreakEvent();
    }

    /**
     * 注册语言管理器
     */
    private void registerLanguageManager() {

        ClientPlayConnectionEvents.JOIN.register(IronFistNew.IRONFISTNEW, (handler, sender, client) -> {
            LanguageManager languageManager = client.getLanguageManager();
            String language = languageManager.getLanguage();
            MyLanguageManager.init(language);
        });
    }


    /**
     * 注册命令 fist
     */
    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("fist")
                    .then(ClientCommandManager.literal("addxp")
                            .then(ClientCommandManager.argument("xp", DoubleArgumentType.doubleArg(0)).executes(IronFistCommand::addXp).requires(source -> source.hasPermissionLevel(1))))
                    .then(ClientCommandManager.literal("levelup").executes(IronFistCommand::levelUp).requires(source -> source.hasPermissionLevel(1)))
                    .then(ClientCommandManager.literal("showxp").executes(IronFistCommand::showXp))
                    .then(ClientCommandManager.literal("showlevel").executes(IronFistCommand::showLevel))
                    .then(ClientCommandManager.literal("debug")
                            .then(ClientCommandManager.literal("info").executes(IronFistCommand::debugInfo)))
                    .executes(IronFistCommand::getCommandUsage));
        });
    }

    /**
     * 处理方块破坏事件
     */
    private void registerBlockBreakEvent() {
        BlockBreakService blockBreakService = BlockBreakService.INSTANCE;
        // 非空手恢复挖掘速度
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (BlockBreakService.canExecute(player)) {
                // 可以挖掘调整速度
                blockBreakService.setBlockBreakSpeed(player, ironFistPlayer.getFistLevel());
            } else {
                blockBreakService.setBlockBreakSpeed(player, 1);
            }
            return ActionResult.PASS;
        });
    }

    /**
     * 注册数据包接收器
     */
    private void registerGlobalReceiver() {

        // 注册payload
        PayloadTypeRegistry.playS2C().register(MyS2CSyncPayload.ID, MyS2CSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(MyS2CInitPayload.ID, MyS2CInitPayload.CODEC);

        // 初始化
        ClientPlayNetworking.registerGlobalReceiver(MyS2CInitPayload.ID, (payload, context) -> {
            ironFistPlayer = PayloadUtil.decodePayload(payload);
        });

        // 更新数据
        ClientPlayNetworking.registerGlobalReceiver(MyS2CSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                // 拿到服务端发送的数据包
                ironFistPlayer = PayloadUtil.decodePayload(payload);

                // 调试模式信息
                if (IronFistCommand.debugInfo) {
                    ClientPlayerEntity player = context.player();
                    player.sendMessage(Text.literal(ironFistPlayer.getFistLevel() + " " + ironFistPlayer.getFistXp() + " " +
                            ironFistPlayer.getEnergy() + " " + ironFistPlayer.getCumulativeWork() + " " + ironFistPlayer.getLastBreakMillis()),false);
                }

            });
        });
    }
}
