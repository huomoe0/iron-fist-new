package top.azusall.ironfistnew.mixin.client;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.resource.language.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.azusall.ironfistnew.lang.MyLanguageManager;

/**
 * @author houmo
 */
@Mixin(LanguageManager.class)
@Slf4j
public class LanguageMixin {

    @Inject(method = "setLanguage", at = @At(value = "HEAD"))
    public void updateMyLanguageManager(String languageCode, CallbackInfo ci) {
        MyLanguageManager.init(languageCode);
    }
}
