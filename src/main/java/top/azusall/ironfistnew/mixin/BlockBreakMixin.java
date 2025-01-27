package top.azusall.ironfistnew.mixin;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.azusall.ironfistnew.service.BlockBreakService;

/**
 * @Author: houmoe
 * @Date: 2024/10/25 01:17
 * @Description: 修改方块挖掘掉落
 */
@Slf4j
@Mixin(value = ServerPlayerInteractionManager.class)
public abstract class BlockBreakMixin {

    /**
     * 让方块掉落
     */
    @Redirect(method = "tryBreakBlock",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;canHarvest(Lnet/minecraft/block/BlockState;)Z"))
    public boolean canHarvestReturn(ServerPlayerEntity instance, BlockState blockState) {
        BlockBreakService instance1 = BlockBreakService.INSTANCE;
        return instance1.canHarvest(instance, blockState);
    }

}
