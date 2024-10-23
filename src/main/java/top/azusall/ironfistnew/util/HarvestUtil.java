package top.azusall.ironfistnew.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liumingda
 * @Date: 2024/10/19 00:20
 * @Description:
 */
public class HarvestUtil {
    private static final Map<Block, Integer> HARVEST_LEVELS = new HashMap<>();

    static {
        HARVEST_LEVELS.put(Blocks.STONE, 1);
        HARVEST_LEVELS.put(Blocks.DIAMOND_ORE, 2);
        // 添加其他方块及其挖掘等级
    }

    public static int getHarvestLevel(Block block) {
        return HARVEST_LEVELS.getOrDefault(block, 0); // 默认值为 0
    }
}
