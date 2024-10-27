package top.azusall.ironfistnew.mixin.client;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.azusall.ironfistnew.service.BlockBreakService;

/**
 * @author huomoe
 */
@Slf4j
@Mixin(Item.class)
public class ItemMixin {



    @Inject(method = "getMiningSpeedMultiplier", at = @At(value = "HEAD"), cancellable = true)
    private void getMiningSpeedMultiplier(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue((float) BlockBreakService.baseMiningSpeedMultiplier);
    }


}
