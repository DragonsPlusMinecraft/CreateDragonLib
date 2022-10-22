package plus.dragons.createdragonlib.advancement;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

public class AdvancementFactory {
     private final String namespace;
    private final TriggerFactory triggerFactory;

    public AdvancementFactory(String namespace) {
        this.namespace = namespace;
        this.triggerFactory = new TriggerFactory();
    }

    public Advancement.Builder create(String id) {
        return new Advancement.Builder(namespace,id,triggerFactory);
    }

    public void registerDatagen(final GatherDataEvent event) {
        DataGenerator datagen = event.getGenerator();
        datagen.addProvider(true,new AdvancementDatagen(namespace,datagen));
    }

    public TriggerFactory getTriggerFactory(){
        return triggerFactory;
    }
}
