package top.azusall.ironfistnew.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import top.azusall.ironfistnew.client.IronFistNewClient;
import top.azusall.ironfistnew.client.util.ClientPayloadUtil;
import top.azusall.ironfistnew.entity.MyC2SSyncPayload;
import top.azusall.ironfistnew.service.BlockBreakService;

/**
 * @author houmo
 */
@Slf4j
public class IronFistCommand implements Command<FabricClientCommandSource> {
    /**
     * 命令列表
     */
    private static final String[] COMMANDS = new String[]{"addxp", "levelup", "showxp", "showlevel"};


    public static int addXp(CommandContext<FabricClientCommandSource> ctx) {
        double newXp = IronFistNewClient.ironFistPlayer.getFistXp() + DoubleArgumentType.getDouble(ctx, "xp");
        IronFistNewClient.ironFistPlayer.setFistXp(newXp);
        ClientPayloadUtil.sendToClient(IronFistNewClient.ironFistPlayer, MyC2SSyncPayload.class);
        ctx.getSource().sendFeedback(Text.literal("经验提升到: " + newXp));
        return 1;
    }

    public static int levelUp(CommandContext<FabricClientCommandSource> ctx) {
        int oldLevel = IronFistNewClient.ironFistPlayer.getFistLevel();
        int newLevel = oldLevel + 1;
        ClientPlayerEntity player = ctx.getSource().getPlayer();
        assert player != null;

        IronFistNewClient.ironFistPlayer.setFistLevel(newLevel);
        IronFistNewClient.ironFistPlayer.setFistXp(BlockBreakService.INSTANCE.getLevelUpXp(oldLevel));
        ClientPayloadUtil.sendToClient(IronFistNewClient.ironFistPlayer, MyC2SSyncPayload.class);
        player.sendMessage(Text.literal("拳头升级了! 当前等级: " + newLevel));
        return 1;
    }

    public static int showXp(CommandContext<FabricClientCommandSource> ctx) {
        ctx.getSource().sendFeedback(Text.literal("拳头经验: " + IronFistNewClient.ironFistPlayer.getFistXp() +
                "\n下一级所需经验: " + BlockBreakService.INSTANCE.getLevelUpXp(IronFistNewClient.ironFistPlayer.getFistLevel())));
        return 1;
    }

    public static int showLevel(CommandContext<FabricClientCommandSource> ctx) {
        ctx.getSource().sendFeedback(Text.literal("拳头等级: " + IronFistNewClient.ironFistPlayer.getFistLevel()));
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
        String usage = "/fist [";
        for (int i = COMMANDS.length - 1; i >= 0; i--) {
            usage += COMMANDS[i];
            if (i >= 1) {
                usage += " | ";
            }
        }
        usage += "]";
        // 打印用法
        String finalUsage = usage;
        ctx.getSource().sendFeedback(Text.literal(finalUsage));
        return 1;
    }
}
