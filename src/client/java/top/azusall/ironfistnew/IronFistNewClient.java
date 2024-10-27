package top.azusall.ironfistnew;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import top.azusall.ironfistnew.command.IronFistCommand;
import top.azusall.ironfistnew.config.IronFistNewConfig;
import top.azusall.ironfistnew.entity.IronFistPlayer;
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
        IronFistNewConfig.loadConfig(FabricLoaderImpl.INSTANCE.getConfigDir().toString());
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
                    .then(ClientCommandManager.literal("addxp").requires(source -> source.hasPermissionLevel(1))
                            .then(ClientCommandManager.argument("xp", DoubleArgumentType.doubleArg(0)).executes(IronFistCommand::addXp)))
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
        // 非空手恢复挖掘速度
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (BlockBreakService.canExecute(player)) {
                // 可以挖掘调整速度
                BlockBreakService.setBlockBreakSpeed(player, ironFistPlayer.getFistLevel());
            } else {
                BlockBreakService.setBlockBreakSpeed(player, 1);
            }
            return ActionResult.PASS;
        });
    }

    /**
     * 注册数据包接收器
     */
    private void registerGlobalReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(IronFistNew.IRONFISTNEW, (MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf payload, PacketSender sender) -> {
            ironFistPlayer = PayloadUtil.decodePayload(payload);

            // 调试模式信息
                if (IronFistCommand.debugInfo) {
                    ClientPlayerEntity player = client.player;
                    player.sendMessage(Text.literal(ironFistPlayer.getFistLevel() + " " + ironFistPlayer.getFistXp() + " " +
                            ironFistPlayer.getEnergy() + " " + ironFistPlayer.getCumulativeWork() + " " + ironFistPlayer.getLastBreakMillis()), false);
                }
        });
    }
}
