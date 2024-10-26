package top.azusall.ironfistnew.util;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

/**
 * @author: huomoe
 */
@Slf4j
public class MessageUtil {

    public static void sendToPlayer(PlayerEntity player, String msg) {
        player.sendMessage(Text.literal(msg), false);
    }
}
