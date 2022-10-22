package plus.dragons.createdragonlib.advancement;

import com.google.common.collect.Sets;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class AdvancementDatagen implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String namespace;
    private final DataGenerator generator;

    public AdvancementDatagen(String namespace, DataGenerator generator) {
        this.namespace = namespace;
        this.generator = generator;
    }

    @Override
    public void run(CachedOutput cache) {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<net.minecraft.advancements.Advancement> consumer = advancement -> {
            if (!set.add(advancement.getId()))
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            Path advancementPath = path.resolve("data/"
                + advancement.getId().getNamespace() + "/advancements/"
                + advancement.getId().getPath() + ".json"
            );
            try {
                DataProvider.saveStable(cache, advancement.deconstruct().serializeToJson(), advancementPath);
            } catch (IOException ioexception) {
                LOGGER.error("Couldn't save advancement {}", advancementPath, ioexception);
            }
        };
        var advancements = Advancement.ENTRIES_MAP.get(namespace);
        if(advancements!=null)
            for (var advancement :advancements) {
                advancement.save(consumer);
            }

    }

    @Override
    public String getName() {
        return "Create Dragon Lib";
    }

}
