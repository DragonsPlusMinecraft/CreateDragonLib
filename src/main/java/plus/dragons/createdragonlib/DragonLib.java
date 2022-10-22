package plus.dragons.createdragonlib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plus.dragons.createdragonlib.fluid.FluidReactionEvent;

@Mod(DragonLib.ID)
public class DragonLib {
    public static final String ID = "create_dragon_lib";
    private static final Logger LOGGER = LogManager.getLogger();

    public DragonLib() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, FluidReactionEvent::postOnFluidPlaceBlockEvent);
        LOGGER.info("Create: Dragon Lib " +
            ModLoadingContext.get().getActiveContainer().getModInfo().getVersion() +
            " has initialized, ready to support your Create add-ons!"
        );
    }

}
