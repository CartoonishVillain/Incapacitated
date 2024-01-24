package com.cartoonishvillain.incapacitated.mixin;


import com.cartoonishvillain.incapacitated.FabricIncapacitated;
import com.cartoonishvillain.incapacitated.component.IncapacitatedComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.cartoonishvillain.incapacitated.component.ComponentStarter.INCAPACITATEDCOMPONENTINSTANCE;

@Mixin(LocalPlayer.class)
public class LocalPlayerTickMixin {
    @Inject(at = @At("HEAD"), method = "tick")
    private void incapacitatedLocalTick(CallbackInfo ci){
        IncapacitatedComponent playerData = INCAPACITATEDCOMPONENTINSTANCE.get((LocalPlayer) (Object) this);
        if (FabricIncapacitated.lastDownDesaturate && playerData.getDownsUntilDeath() <= 0) {
            ResourceLocation resourceLocation = new ResourceLocation("shaders/post/desaturate.json");
            ((LoadEffectInvoker) Minecraft.getInstance().gameRenderer).incapacitatedLoadEffect(resourceLocation);
        } else {
            Minecraft.getInstance().gameRenderer.shutdownEffect();
        }
    }
}
