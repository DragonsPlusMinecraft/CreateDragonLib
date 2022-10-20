package plus.dragons.createdragonlib.mixin;

import com.simibubi.create.foundation.advancement.CreateAdvancement;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonlib.advancement.ModAdvancement;
import plus.dragons.createdragonlib.advancement.ModdedCreateAdvancement;

@Mixin(value = CreateAdvancement.class, remap = false)
@Implements(@Interface(iface = ModdedCreateAdvancement.class, prefix = "createDragonLib$", remap = Interface.Remap.NONE))
public class CreateAdvancementMixin {
    
    @Nullable private ModAdvancement createDragonLib$advancement = null;
    
    public void createDragonLib$fromModAdvancement(ModAdvancement advancement) {
        this.createDragonLib$advancement = advancement;
    }
    
    @Inject(method = "isAlreadyAwardedTo", at = @At("HEAD"), cancellable = true)
    private void enchantmentIndustryIsAlreadyAwardedTo(Player player, CallbackInfoReturnable<Boolean> cir) {
        if(createDragonLib$advancement != null) {
            cir.setReturnValue(createDragonLib$advancement.isAlreadyAwardedTo(player));
        }
    }
    
    @Inject(method = "awardTo", at = @At("HEAD"), cancellable = true)
    private void enchantmentIndustryAwardTo(Player player, CallbackInfo ci) {
        if(createDragonLib$advancement != null) {
            createDragonLib$advancement.awardTo(player);
            ci.cancel();
        }
    }
    
}
