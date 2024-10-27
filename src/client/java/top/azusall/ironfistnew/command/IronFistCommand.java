package top.azusall.ironfistnew.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import top.azusall.ironfistnew.IronFistNewClient;
import top.azusall.ironfistnew.lang.MyLanguageManager;
import top.azusall.ironfistnew.util.ClientPayloadUtil;
import top.azusall.ironfistnew.service.BlockBreakService;
import top.azusall.ironfistnew.util.MessageUtil;

/**
 * @author houmo
 */
@Slf4j
public class IronFistCommand implements Command<FabricClientCommandSource> {
    /**
     * 命令列表
     */
    private static final String[] COMMANDS = new String[]{"addxp", "levelup", "showxp", "showlevel"};

    /**
     * 调试模式开关
     */
    public static boolean debugInfo = false;

    public static int addXp(CommandContext<FabricClientCommandSource> ctx) {
        double newXp = IronFistNewClient.ironFistPlayer.getFistXp() + DoubleArgumentType.getDouble(ctx, "xp");
        IronFistNewClient.ironFistPlayer.setFistXp(newXp);
        ClientPayloadUtil.sendToClient(IronFistNewClient.ironFistPlayer);
        MessageUtil.sendToPlayer(ctx.getSource().getPlayer(), MyLanguageManager.getText("ironfistnew.command.addXp", newXp));
        return 1;
    }

    public static int levelUp(CommandContext<FabricClientCommandSource> ctx) {
        int oldLevel = IronFistNewClient.ironFistPlayer.getFistLevel();
        int newLevel = oldLevel + 1;
        ClientPlayerEntity player = ctx.getSource().getPlayer();
        assert player != null;

        IronFistNewClient.ironFistPlayer.setFistLevel(newLevel);
        IronFistNewClient.ironFistPlayer.setFistXp(BlockBreakService.getLevelUpXp(oldLevel));
        ClientPayloadUtil.sendToClient(IronFistNewClient.ironFistPlayer);
        MessageUtil.sendToPlayer(player, MyLanguageManager.getText("ironfistnew.command.levelUp", newLevel));
        return 1;
    }

    public static int showXp(CommandContext<FabricClientCommandSource> ctx) {
        MessageUtil.sendToPlayer(ctx.getSource().getPlayer(), MyLanguageManager.getText("ironfistnew.command.showXp",
                IronFistNewClient.ironFistPlayer.getFistXp(), BlockBreakService.getLevelUpXp(IronFistNewClient.ironFistPlayer.getFistLevel())));
        return 1;
    }

    public static int showLevel(CommandContext<FabricClientCommandSource> ctx) {
        MessageUtil.sendToPlayer(ctx.getSource().getPlayer(), MyLanguageManager.getText("ironfistnew.command.showLevel", IronFistNewClient.ironFistPlayer.getFistLevel()));
        return 1;
    }

    public static int debugInfo(CommandContext<FabricClientCommandSource> ctx) {
        debugInfo = !debugInfo;
        MessageUtil.sendToPlayer(ctx.getSource().getPlayer(), MyLanguageManager.getText("ironfistnew.command.debugInfo", debugInfo));
        return 1;
    }

    @Override
    public int run(CommandContext<FabricClientCommandSource> ctx) {
        return 0;
    }

    /**
     * 获取命令用法
     */
    public static int getCommandUsage(CommandContext<FabricClientCommandSource> ctx) {
        StringBuilder usage = new StringBuilder("/fist [");
        for (int i = COMMANDS.length - 1; i >= 0; i--) {
            usage.append(COMMANDS[i]);
            if (i >= 1) {
                usage.append(" | ");
            }
        }
        usage.append("]");
        // 打印用法
        String finalUsage = usage.toString();
        ctx.getSource().sendFeedback(Text.literal(finalUsage));
        return 1;
    }
}
