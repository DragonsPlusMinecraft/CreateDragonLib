package plus.dragons.createdragonlib.fluid;

import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Event to control fluid reactions in pipe/world. <br>
 * <br>
 * When both fluids are in pipe, they are both considered "flowing",
 * and there is no ordering between {@link FluidReactionEvent#fluidA}/{@link FluidReactionEvent#fluidB}. <br>
 * <br>
 * When a fluid in pipe is leaking into the world to meet another fluid,
 * {@link FluidReactionEvent#fluidA} represents the leaking fluid and is considered "flowing",
 * {@link FluidReactionEvent#fluidB} represents the fluid in world. <br>
 * <br>
 * When both fluids are in world,
 * {@link FluidReactionEvent#fluidA} is always considered as {@link Fluids#FLOWING_LAVA},
 * {@link FluidReactionEvent#fluidB} is the fluid that the lava is going to react with,
 * the original result should be {@link Blocks#STONE}. <br>
 * <br>
 * This event is {@link Cancelable}.
 * If canceled, this will prevent other listeners from changing the result.
 */
@Cancelable
public class FluidReactionEvent extends Event {
    private final Fluid fluidA;
    private final Fluid fluidB;
    @Nullable
    private BlockState state = null;
    
    public FluidReactionEvent(Fluid fluidA, Fluid fluidB) {
        this.fluidA = fluidA;
        this.fluidB = fluidB;
    }
    
    public void setBlockState(BlockState state) {
        this.state = state;
    }
    
    @Nullable
    public BlockState getBlockState() {
        return this.state;
    }
    
    @ApiStatus.Internal
    public static void postOnFluidPlaceBlockEvent(BlockEvent.FluidPlaceBlockEvent event) {
        BlockState blockState = event.getOriginalState();
        if (blockState.is(Blocks.FIRE))
            return;
        
        FluidState fluidState = blockState.getFluidState();
        if (fluidState.is(Fluids.LAVA))
            return;
        
        BlockPos pos = event.getPos();
        LevelAccessor world = event.getLevel();
        for (Direction direction : Iterate.directions) {
            FluidState metFluidState = fluidState.isSource()
                ? fluidState
                : world.getFluidState(pos.relative(direction));
            if (!metFluidState.is(FluidTags.WATER))
                continue;
            FluidReactionEvent reaction = new FluidReactionEvent(Fluids.FLOWING_LAVA, metFluidState.getType());
            MinecraftForge.EVENT_BUS.post(event);
            if (reaction.state == null)
                continue;
            event.setNewState(reaction.state);
            break;
        }
    }
    
}
