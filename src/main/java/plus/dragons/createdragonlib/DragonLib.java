package plus.dragons.createdragonlib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import plus.dragons.createdragonlib.api.event.FluidLavaInteractionRegisterEvent;

@Mod(DragonLib.MOD_ID)
public class DragonLib {
    public static final String MOD_ID = "create_dragon_lib";

    public DragonLib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(DragonLib::init);
    }

    private static void init(final FMLDedicatedServerSetupEvent event) {
        MinecraftForge.EVENT_BUS.post(new FluidLavaInteractionRegisterEvent());
    }
}
