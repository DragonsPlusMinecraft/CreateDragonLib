package plus.dragons.createdragonlib.foundation.mixin;

import com.simibubi.create.AllFluids;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonlib.api.event.FluidLavaInteractionRegisterEvent;

@Mixin(value = AllFluids.class, remap = false)
public class AllFluidsMixin {
    
    @Inject(method = "getLavaInteraction", at = @At("HEAD"), cancellable = true)
    private static void enchantmentIndustry$handleInkLavaInteraction(FluidState fluidState, CallbackInfoReturnable<BlockState> cir) {
        for(var e: FluidLavaInteractionRegisterEvent.REACTIONS.entrySet())
            if(e.getKey().test(fluidState))
                cir.setReturnValue(e.getValue());
    }
    
}
