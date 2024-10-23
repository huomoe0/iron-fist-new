package top.azusall.ironfistnew.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class CommandIronFist implements Command<ServerCommandSource> {
    /**
     * 命令列表
     */
    private static String[] commands = new String[]{"addxp", "levelup", "showxp", "showlevel"};


    public static int addXp(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(() -> Text.literal("Called /fist addxp."), false);
        return 1;
    }

    public static int levelUp(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(() -> Text.literal("Called /fist levelup."), false);
        return 1;
    }

    public static int showXp(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(() -> Text.literal("Called /fist showxp."), false);
        return 1;
    }

    public static int showLevel(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(() -> Text.literal("Called /fist showlevel."), false);
        return 1;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource source = ctx.getSource();
        return 0;
    }

    /**
     * @return 获取命令用法
     */
    public static int getCommandUsage(CommandContext<ServerCommandSource> ctx) {
        String usage = "/fist [";
        for (int i = commands.length - 1; i >= 0; i--) {
            usage += commands[i];
            if (i >= 1) {
                 usage += " | ";
            }
        }
        usage += "]";
        // 打印用法
        String finalUsage = usage;
        ctx.getSource().sendFeedback(() -> Text.literal(finalUsage), false);
        return 1;
    }
//
//	/**
//	 * Adds the strings available in this command to the given list of tab
//	 * completion options.
//	 *
//	 * @param sender
//	 *            The command sender data
//	 * @param args
//	 *            The command arguments
//	 */
//	@Override
//	public List<String> addTabCompletionOptions(ICommandSender sender,
//			String[] args) {
//		ArrayList<String> options = new ArrayList<String>();
//
//		// Only process if at least one argument is (partially) given
//		if (args.length == 1) {
//			// If a command could be made out of the current argument,
//			// add it to the list
//			for (String command : commands) {
//				if (command.startsWith(args[0])) {
//					options.add(command);
//				}
//			}
//		}
//
//		return options;
//	}

//    /**
//     * Processes an issued command.
//     *
//     * @param sender The command sender data
//     * @param args   The command arguments
//     */
//    public void processCommand(ICommandSender sender, String[] args) {
//        if (args.length == 1) {
//            if (args[0].equals("showlevel")) {
//                EntityPlayerMP player = getPlayer(sender,
//                        sender.getCommandSenderName());
//
//                // Load the iron fist properties
//                IronFistPlayer properties = IronFistPlayer.get(player);
//                int fistLevel = properties.getFistLevel();
//
//                // Show the fist level
//                player.addChatMessage(new ChatComponentText("Fist level: "
//                        + fistLevel));
//                return;
//            } else if (args[0].equals("showxp")) {
//                EntityPlayerMP player = getPlayer(sender,
//                        sender.getCommandSenderName());
//
//                // Load the iron fist properties
//                IronFistPlayer properties = IronFistPlayer.get(player);
//                double fistXP = properties.getFistXP();
//
//                // Show the fist level
//                player.addChatMessage(new ChatComponentText("Fist xp: "
//                        + fistXP));
//                return;
//            } else if (args[0].equals("levelup")) {
//                EntityPlayerMP player = getPlayer(sender,
//                        sender.getCommandSenderName());
//
//                // Load the iron fist properties
//                IronFistPlayer properties = IronFistPlayer.get(player);
//
//                // Increase the fist level by one
//                int fistLevel = properties.getFistLevel();
//                fistLevel++;
//                properties.setFistLevel(fistLevel);
//
//                player.addChatMessage(new ChatComponentText("Fist level up!"));
//                player.addChatMessage(new ChatComponentText("New level: "
//                        + fistLevel));
//                return;
//            }
//        } else if (args.length == 2) {
//            if (args[0].equals("addxp")) {
//                EntityPlayerMP player = getPlayer(sender,
//                        sender.getCommandSenderName());
//
//                // Load the iron fist properties
//                IronFistPlayer properties = IronFistPlayer.get(player);
//                double currentXP = properties.getFistXP();
//                double additionalXP = 0.0D;
//                try {
//                    additionalXP = Double.parseDouble(args[1]);
//                } catch (NumberFormatException e) {
//                    player.addChatMessage(new ChatComponentText("Invalid number!"));
//                }
//
//                // Set the new XP
//                double newXP = currentXP + additionalXP;
//                properties.setFistXP(newXP);
//                player.addChatMessage(new ChatComponentText("XP increased to " + newXP));
//                return;
//            }
//        }
//
//        throw new WrongUsageException(this.getCommandUsage(sender),
//                new Object[0]);
//    }


}
