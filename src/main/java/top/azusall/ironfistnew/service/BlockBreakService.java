package top.azusall.ironfistnew.service;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import top.azusall.ironfistnew.constant.ParamConstant;
import top.azusall.ironfistnew.entity.IronFistPlayer;
import top.azusall.ironfistnew.util.HarvestUtil;

/**
 * @Author: liumingda
 * @Date: 2024/10/19 09:41
 * @Description:
 */

@Slf4j
public class BlockBreakService {
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
        if (fistXp > getLevelUpXp(fistLevel)) {
            fistLevel++;
            player.sendMessage(Text.literal(ParamConstant.LEVEL_UP_MESSAGE_PREFIX + fistLevel));
            playerState.setFistLevel(fistLevel);
        }

        // 保存属性
        playerState.setLastBreakMillis(currentMillis);
        playerState.setFistXp(fistXp);
        playerState.setCumulativeWork(cumulativeWork);
        playerState.setEnergy(energy);
    }


    /**
     * @param fistLevel 当前等级
     * @return 升级需要的经验
     */
    public double getLevelUpXp(int fistLevel) {
        return 6.95997 * Math.pow(Math.E, (1.97241 * fistLevel));
    }


    /**
     * 钩入破坏速度计算，并根据拳头等级进行修改。
     * 根据是否需要工具以及达到的等级来决定速度。
     */

    public void onBreakSpeed(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction, IronFistPlayer playerState) {
        int fistLevel = playerState.getFistLevel();
        Block block = world.getBlockState(pos).getBlock();
        BlockEntity blockEntity = world.getBlockEntity(pos);
//        player.getBlockBreakingSpeed()
        // 如果需要工具且已达到所需等级，或不需要工具，则更新速度
        if (fistLevel - 1 >= HarvestUtil.getHarvestLevel(block)) {
//            setNewSpeed(Math.max(((fistLevel - 1) * 2), 1f));
        }

    }

//
//    /**
//     * Hooks in to the check to see if a player can harvest a block and uses the
//     * fist level to determine it.
//     *
//     * @param event The can harvest check data
//     */
//    @SubscribeEvent
//    public void canHarvestBlock(PlayerEvent.HarvestCheck event) {
//        if (event.entityPlayer == null) {
//            return;
//        }
//
//        // Load the iron fist properties of the player
//        IronFistPlayer properties = IronFistPlayer.get(event.entityPlayer);
//        int fistLevel = properties.getFistLevel();
//
//        // If a tool is required and the required level is reached the block may
//        // be harvested, meta data 0 is used as I could find no way to find the
//        // actual metadata.
//        if (!event.block.getMaterial().isToolNotRequired()) {
//            int harvestLevel = event.block.getHarvestLevel(0);
//
//            // If the harvest level is -1 check by material else check if level
//            // is
//            // reached
//            if (harvestLevel == 0
//                    && canBreakMaterial(fistLevel, event.block.getMaterial())
//                    || (harvestLevel != 0 && harvestLevel - (fistLevel - 1) <= 0)) {
//                event.success = true;
//            }
//        }
//    }
//
//    /**
//     * Checks if the fist level can break the material.
//     *
//     * @param level    The fist level to check against
//     * @param material The material to check for
//     * @return Whether or not the material can be broken
//     */
//    private boolean canBreakMaterial(int level, Material material) {
//        return (material == Material.rock || material == Material.iron || material == Material.anvil) ? level > 1
//                : true;
//    }
}
