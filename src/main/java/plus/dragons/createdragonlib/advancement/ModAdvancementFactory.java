package plus.dragons.createdragonlib.advancement;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModAdvancementFactory {
    private final String namespace;

    public static ModAdvancementFactory create(String namespace){
        return new ModAdvancementFactory(namespace);
    }

    ModAdvancementFactory(String namespace) {
        this.namespace = namespace;
    }

    public ModAdvancement.Builder builder(String id) {
        return new ModAdvancement.Builder(namespace,id);
    }

    public void registerDatagen(final GatherDataEvent event) {
        DataGenerator datagen = event.getGenerator();
        datagen.addProvider(true,new ModAdvancementGen(namespace,datagen));
    }
}
