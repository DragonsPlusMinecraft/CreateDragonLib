package plus.dragons.createdragonlib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import plus.dragons.createdragonlib.api.event.FluidLavaInteractionRegisterEvent;
import plus.dragons.createdragonlib.foundation.data.advancement.ModTriggerFactory;

@Mod(DragonLib.MOD_ID)
public class DragonLib {
    public static final String MOD_ID = "create_dragon_lib";

    public DragonLib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(DragonLib::init);
    }

    private static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModTriggerFactory.register();
            MinecraftForge.EVENT_BUS.post(new FluidLavaInteractionRegisterEvent());
        });
    }
}
