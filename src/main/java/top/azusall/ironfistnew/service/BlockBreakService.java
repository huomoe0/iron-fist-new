package top.azusall.ironfistnew.service;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.BlockState;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.azusall.ironfistnew.constant.ParamConstant;
import top.azusall.ironfistnew.entity.IronFistPlayer;

/**
 * @Author: liumingda
 * @Date: 2024/10/19 09:41
 * @Description:
 */

@Slf4j
public class BlockBreakService {
    public static final BlockBreakService INSTANCE = new BlockBreakService();

    private BlockBreakService() {
    }

    /**
     * 处理拳头相关逻辑
     */
    public void onBlockBreak(PlayerEntity player, World world, BlockPos pos, BlockState state, IronFistPlayer playerState) {
        int fistLevel = playerState.getFistLevel();
        float cumulativeWork = playerState.getCumulativeWork();
        // 硬度
        float hardness = state.getHardness(world, pos);
        // 精力恢复时间
        float recoveryTime = ParamConstant.ENERGY_RECOVERY_FACTOR * fistLevel;
        long currentMillis = System.currentTimeMillis();
        long lastMillis = playerState.getLastBreakMillis();
        long deltaMillis = (currentMillis - lastMillis);
        long delta = Math.round(Math.min(recoveryTime, deltaMillis));

        float workTime = ParamConstant.MILLISECONDS_HARDNESS_ONE * hardness;
        float restTime = delta - workTime;

        if (delta == recoveryTime) {
            cumulativeWork = 0;
        } else {
            cumulativeWork = Math.min(
                    Math.max(0, workTime - restTime + cumulativeWork),
                    recoveryTime);
        }

        double energy = (recoveryTime - cumulativeWork) / recoveryTime;

        // 如果疲劳值太低会受到伤害
        if (energy < ParamConstant.ENERGY_THRESHOLD) {
            // 检查当前生命值，确保不会致死
            if (player.getHealth() - ParamConstant.DAMAGE_AMOUNT > 0) {
                player.damage(world.getDamageSources().generic(), ParamConstant.DAMAGE_AMOUNT);
                log.info("------------------------player.damage(world.getDamageSources().generic(), ParamConstant.DAMAGE_AMOUNT) -----------------------------");
            } else {
                // 如果生命值不足以承受该伤害，设置为最低生命值
                player.setHealth(ParamConstant.MIN_HEALTH);
            }
            player.sendMessage(Text.literal(ParamConstant.BLEEDING_MESSAGE), false);
        }

        // 增加经验并检查是否升级
        double fistXp = playerState.getFistXp();
        double obtainedXp = hardness * energy;
        fistXp += obtainedXp;
        if (fistXp >= getLevelUpXp(fistLevel)) {
            fistLevel++;
            player.sendMessage(Text.literal(ParamConstant.LEVEL_UP_MESSAGE_PREFIX + fistLevel));
        }

        // 保存属性
        playerState.setFistLevel(fistLevel);
        playerState.setLastBreakMillis(currentMillis);
        playerState.setFistXp(fistXp);
        playerState.setCumulativeWork(cumulativeWork);
        playerState.setEnergy(energy);
    }

    /**
     * @param nowLevel 当前等级
     * @return 升级需要的经验
     */
    public double getLevelUpXp(int nowLevel) {
        return 6.95997 * Math.pow(Math.E, (1.97241 * nowLevel));
    }


    /**
     * 钩入破坏速度计算，并根据拳头等级进行修改。
     * 根据是否需要工具以及达到的等级来决定速度。
     */
    public void setBlockBreakSpeed(PlayerEntity player, int level) {
        // /attribute @s minecraft:player_block_break_speed base set 5.0
        // 修改挖掘速度
        AttributeContainer attributes = player.getAttributes();
        EntityAttributeInstance customInstance = attributes.getCustomInstance(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED);
        double newSpeed = Math.max(((level - 1) * ParamConstant.SPEED_MULTIPLE), 1f);
        assert customInstance != null;
        customInstance.setBaseValue(newSpeed);
    }


    private ItemStack getFistLevelTool(int level) {
        Item pickaxe = switch (level) {
            case 1 -> Items.AIR;
            case 2 -> Items.WOODEN_PICKAXE;
            case 3 -> Items.STONE_PICKAXE;
            case 4 -> Items.IRON_PICKAXE;
            default -> Items.DIAMOND_PICKAXE;
        };
        return pickaxe.getDefaultStack();
    }


    public boolean canHarvest(ServerPlayerEntity instance, BlockState blockState) {
        IronFistPlayer playerState = StateSaverAndLoader.getPlayerState(instance);
        int fistLevel = playerState.getFistLevel();
        return !blockState.isToolRequired() || getFistLevelTool(fistLevel).isSuitableFor(blockState);
    }
}
