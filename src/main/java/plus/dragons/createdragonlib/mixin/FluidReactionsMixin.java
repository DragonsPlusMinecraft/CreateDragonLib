package plus.dragons.createdragonlib.mixin;

import com.simibubi.create.content.contraptions.fluids.FluidReactions;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.createdragonlib.fluid.FluidReactionEvent;

@Mixin(value = FluidReactions.class, remap = false)
public class FluidReactionsMixin {
    
    @Inject(method = "handlePipeFlowCollision", at = @At("HEAD"), cancellable = true)
    private static void handlePipeFlowFluidReaction(Level world, BlockPos pos, FluidStack fluid, FluidStack fluid2, CallbackInfo ci) {
        FluidReactionEvent event = new FluidReactionEvent(
            FluidHelper.convertToFlowing(fluid.getFluid()),
            FluidHelper.convertToFlowing(fluid2.getFluid())
        );
        MinecraftForge.EVENT_BUS.post(event);
        BlockState result = event.getBlockState();
        if (result != null) {
            AdvancementBehaviour.tryAward(world, pos, AllAdvancements.CROSS_STREAMS);
            BlockHelper.destroyBlock(world, pos, 1);
            world.setBlockAndUpdate(pos, result);
            ci.cancel();
        }
    }
    
    @Inject(method = "handlePipeSpillCollision", at = @At("HEAD"), cancellable = true)
    private static void handlePipeSpillFluidReaction(Level world, BlockPos pos, Fluid pipeFluid, FluidState worldFluid, CallbackInfo ci) {
        FluidReactionEvent event = new FluidReactionEvent(FluidHelper.convertToFlowing(pipeFluid), worldFluid.getType());
        MinecraftForge.EVENT_BUS.post(event);
        BlockState result = event.getBlockState();
        if (result != null) {
            world.setBlockAndUpdate(pos, result);
            ci.cancel();
        }
    }
    
}
