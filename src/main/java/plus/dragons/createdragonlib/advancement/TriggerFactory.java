package plus.dragons.createdragonlib.advancement;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class TriggerFactory {
    private final List<CriterionTrigger<?>> TRIGGERS = new ArrayList<>();
    public SimpleTrigger addSimple(ResourceLocation resourceLocation) {
        return add(new SimpleTrigger(resourceLocation));
    }

    public AccumulativeTrigger addAccumulative(ResourceLocation resourceLocation) {
        return add(new AccumulativeTrigger(resourceLocation));
    }

   private <T extends CriterionTrigger<?>> T add(T instance) {
        TRIGGERS.add(instance);
        return instance;
    }

    public void register() {
        TRIGGERS.forEach(CriteriaTriggers::register);
    }

}
