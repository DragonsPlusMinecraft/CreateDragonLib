package plus.dragons.createdragonlib.foundation.data.advancement;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ModTriggerFactory {
    private static final List<CriterionTrigger<?>> TRIGGERS = new ArrayList<>();
    public static SimpleTrigger addSimple(ResourceLocation resourceLocation) {
        return add(new SimpleTrigger(resourceLocation));
    }

    public static AccumulativeTrigger addAccumulative(ResourceLocation resourceLocation) {
        return add(new AccumulativeTrigger(resourceLocation));
    }

    private static <T extends CriterionTrigger<?>> T add(T instance) {
        TRIGGERS.add(instance);
        return instance;
    }

    public static void register() {
        TRIGGERS.forEach(CriteriaTriggers::register);
    }

}
