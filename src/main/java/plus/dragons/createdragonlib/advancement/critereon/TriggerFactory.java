package plus.dragons.createdragonlib.advancement.critereon;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class TriggerFactory {
    private final List<CriterionTrigger<?>> triggers = new ArrayList<>();
    
    public SimpleTrigger simple(ResourceLocation resourceLocation) {
        return add(new SimpleTrigger(resourceLocation));
    }
    
    public AccumulativeTrigger accumulative(ResourceLocation resourceLocation) {
        return add(new AccumulativeTrigger(resourceLocation));
    }

    private <T extends CriterionTrigger<?>> T add(T instance) {
        triggers.add(instance);
        return instance;
    }

    public void register() {
        triggers.forEach(CriteriaTriggers::register);
    }

}
