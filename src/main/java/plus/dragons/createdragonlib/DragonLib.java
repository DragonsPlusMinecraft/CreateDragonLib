package plus.dragons.createdragonlib;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plus.dragons.createdragonlib.tag.TagGen;

@Mod(DragonLib.ID)
public class DragonLib {
    public static final String ID = "create_dragon_lib";
    private static final Logger LOGGER = LogManager.getLogger();

    public DragonLib() {
        LOGGER.info("Create: Dragon Lib " +
            ModLoadingContext.get().getActiveContainer().getModInfo().getVersion() +
            " has initialized, ready to support your Create add-ons!"
        );

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(EventPriority.LOWEST, DragonLib::gatherData);
    }

    public static void gatherData(GatherDataEvent event) {
        TagGen.genAll();
    }

}
