package plus.dragons.createdragonlib.advancement;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import plus.dragons.createdragonlib.advancement.critereon.TriggerFactory;

public class AdvancementFactory {
    private final String modid;
    private final AdvancementGen advancementGen;
    private final TriggerFactory triggerFactory = new TriggerFactory();

    public AdvancementFactory(String name, String modid) {
        this.modid = modid;
        this.advancementGen = new AdvancementGen(name, modid);
    }

    public AdvancementHolder.Builder builder(String id) {
        return new AdvancementHolder.Builder(modid, id, triggerFactory);
    }
    
    public TriggerFactory getTriggerFactory() {
        return triggerFactory;
    }

    public void register(IEventBus modEventBus) {
        triggerFactory.register();
        modEventBus.<GatherDataEvent>addListener(event -> {
            DataGenerator datagen = event.getGenerator();
            advancementGen.generator = datagen;
            datagen.addProvider(event.includeServer(), advancementGen);
        });
    }
    
}
