package plus.dragons.createdragonlib.mixin;

import com.simibubi.create.content.fluids.FluidReactions;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.createdragonlib.fluid.FluidLavaReaction;

@Mixin(value = FluidReactions.class, remap = false)
public class FluidReactionsMixin {
    
    @Inject(method = "handlePipeFlowCollision", at = @At("HEAD"), cancellable = true)
    private static void createDragonLib$handlePipeFlowCollision(Level world, BlockPos pos, FluidStack fluid, FluidStack fluid2, CallbackInfo ci) {
        FluidType type = fluid.getFluid().getFluidType();
        FluidType type2 = fluid2.getFluid().getFluidType();
        FluidLavaReaction reaction = null;
        if (type == ForgeMod.LAVA_TYPE.get())
            reaction = FluidLavaReaction.get(type2);
        else if (type2 == ForgeMod.LAVA_TYPE.get())
            reaction = FluidLavaReaction.get(type);
        if (reaction != null) {
            AdvancementBehaviour.tryAward(world, pos, AllAdvancements.CROSS_STREAMS);
            BlockHelper.destroyBlock(world, pos, 1);
            world.setBlockAndUpdate(pos, reaction.withFlowingLava());
            ci.cancel();
        }
    }
    
    @Inject(method = "handlePipeSpillCollision", at = @At("HEAD"), cancellable = true)
    private static void createDragonLib$handleSpillCollision(Level world, BlockPos pos, Fluid pipeFluid, FluidState worldFluid, CallbackInfo ci) {
        FluidType typeP = pipeFluid.getFluidType();
        FluidType typeW = worldFluid.getFluidType();
        BlockState blockState = null;
        if (typeW == ForgeMod.LAVA_TYPE.get()) {
            FluidLavaReaction reaction = FluidLavaReaction.get(typeP);
            if (reaction != null) blockState = worldFluid.isSource() ? reaction.withLava() : reaction.withFlowingLava();
        } else if (typeP == ForgeMod.LAVA_TYPE.get()) {
            FluidLavaReaction reaction = FluidLavaReaction.get(typeW);
            if (reaction != null) blockState = reaction.lavaOnSelf();
        }
        if (blockState != null) {
            world.setBlockAndUpdate(pos, blockState);
            ci.cancel();
        }
    }
    
}
