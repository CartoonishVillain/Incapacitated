package com.cartoonishvillain.incapacitated.mixin;


import com.cartoonishvillain.incapacitated.FabricIncapacitated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerRespawnMixin {
    @Inject(at = @At("HEAD"), method = "respawn")
    private void incapacitatedRespawn(CallbackInfo ci){
        if (FabricIncapacitated.lastDownDesaturate) Minecraft.getInstance().gameRenderer.shutdownEffect();
    }
}
