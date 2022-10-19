package plus.dragons.createdragonlib.api.event;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.eventbus.api.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class FluidLavaInteractionRegisterEvent extends Event {
    public static Map<Predicate<FluidState>, BlockState> REACTIONS = new HashMap<>();

    public void register(Predicate<FluidState> fluidStatePredicate, BlockState resultBlockState){
        REACTIONS.put(fluidStatePredicate,resultBlockState);
    }

}
