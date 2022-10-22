package plus.dragons.createdragonlib.advancement;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import plus.dragons.createdragonlib.advancement.critereon.TriggerFactory;

public class AdvancementFactory {
    private final String modid;
    private final AdvancementGen advancementGen;
    private final TriggerFactory triggerFactory = new TriggerFactory();

    private AdvancementFactory(String name, String modid) {
        this.modid = modid;
        this.advancementGen = new AdvancementGen(name, modid);
    }
    
    public static AdvancementFactory create(String name, String modid) {
        return new AdvancementFactory(name, modid);
    }

    public AdvancementHolder.Builder builder(String id) {
        return new AdvancementHolder.Builder(modid, id, triggerFactory);
    }
    
    public TriggerFactory getTriggerFactory() {
        return triggerFactory;
    }
    
    public void datagen(final GatherDataEvent event) {
        DataGenerator datagen = event.getGenerator();
        advancementGen.generator = datagen;
        datagen.addProvider(event.includeServer(), advancementGen);
    }

    public void register() {
        triggerFactory.register();
    }
    
}
