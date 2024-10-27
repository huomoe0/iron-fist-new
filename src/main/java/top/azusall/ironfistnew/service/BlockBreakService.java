package top.azusall.ironfistnew.service;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.BlockState;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.azusall.ironfistnew.config.IronFistNewConfig;
import top.azusall.ironfistnew.entity.IronFistPlayer;
import top.azusall.ironfistnew.lang.MyLanguageManager;
import top.azusall.ironfistnew.util.MessageUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author houmo
 */
@Slf4j
public class BlockBreakService {
    public static final BlockBreakService INSTANCE = new BlockBreakService();
    public static ArrayList<ArrayList<ItemStack>> levelMap = new ArrayList<>();

    private BlockBreakService() {
    }

    public static void loadLevelList(HashMap<Integer, ArrayList<String>> level) {
        level.forEach((key, value) -> {
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            value.forEach(s -> {
                String[] split = s.split(":");
                Item item = Registries.ITEM.get(Identifier.of(split[0], split[1]));
                itemStacks.add(item.getDefaultStack());
            });
            levelMap.add(itemStacks);
        });
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
        float recoveryTime = IronFistNewConfig.getEnergyRecoveryFactor() * fistLevel;
        long currentMillis = System.currentTimeMillis();
        long lastMillis = playerState.getLastBreakMillis();
        long deltaMillis = (currentMillis - lastMillis);
        long delta = Math.round(Math.min(recoveryTime, deltaMillis));

        float workTime = IronFistNewConfig.getMillisecondsHardnessOne() * hardness;
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
        if (energy < IronFistNewConfig.getEnergyThreshold()) {
            // 检查当前生命值，确保不会致死
            if (player.getHealth() - IronFistNewConfig.getDamageAmount() > 0) {
                player.damage(world.getDamageSources().generic(), IronFistNewConfig.getDamageAmount());
                log.info("------------------------player.damage(world.getDamageSources().generic(), ParamConstant.DAMAGE_AMOUNT) -----------------------------");
            } else {
                // 如果生命值不足以承受该伤害，设置为最低生命值
                player.setHealth(IronFistNewConfig.getMinHealth());
            }
            MessageUtil.sendToPlayer(player, MyLanguageManager.getText("ironfistnew.message.bleeding"));
        }

        // 增加经验并检查是否升级
        double fistXp = playerState.getFistXp();
        double obtainedXp = hardness * energy;
        fistXp += obtainedXp;
        if (fistXp >= getLevelUpXp(fistLevel)) {
            fistLevel++;
            MessageUtil.sendToPlayer(player, MyLanguageManager.getText("ironfistnew.message.levelUp", fistLevel));
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
        double newSpeed = Math.max(((level - 1) * IronFistNewConfig.getSpeedMultiple()), 1f);
        assert customInstance != null;
        customInstance.setBaseValue(newSpeed);
    }


    private ArrayList<ItemStack> getFistLevelTool(int level) {
        if (level >= levelMap.size()) {
            // 默认等级
            level = 0;
        }
        return levelMap.get(level);
    }


    /**
     * 判断当前等级是否可以挖掘方块
     */
    public boolean canHarvest(ServerPlayerEntity instance, BlockState blockState) {
        IronFistPlayer playerState = StateSaverAndLoader.getPlayerState(instance);
        int fistLevel = playerState.getFistLevel();
        boolean b = !blockState.isToolRequired();
        for (ItemStack itemStack : getFistLevelTool(fistLevel)) {
            b |= itemStack.isSuitableFor(blockState);
            if (b)  {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用手上的物品挖掘是否提升经验
     */
    public static boolean canExecute(PlayerEntity player) {
        // 跳过旁观和创造
        if (player.isSpectator() || player.isCreative()) {
            return false;
        }
        // 工具跳过
        Item item = player.getMainHandStack().getItem();
        return !(item instanceof MiningToolItem);
    }
}
