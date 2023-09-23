package plus.dragons.createdragonlib.lang;

import com.google.gson.JsonObject;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import plus.dragons.createdragonlib.advancement.AdvancementHolder;

import java.util.Collections;
import java.util.function.Supplier;

public class LangFactory {
    private final String modid;
    private final LangMerger langMerger;

    private LangFactory(String name, String modid) {
        this.modid = modid;
        this.langMerger = new LangMerger(name, modid);
    }
    
    public static LangFactory create(String name, String modid) {
        return new LangFactory(name, modid);
    }
    
    /**
     * Register the {@link LangMerger} instance to {@link GatherDataEvent}. <br>
     * Should be called in the mod's main class' constructor. <br>
     */
    public void datagen(final GatherDataEvent event) {
        DataGenerator datagen = event.getGenerator();
        langMerger.dataGenerator = datagen;
        datagen.addProvider(event.includeClient(), langMerger);
    }
    
    /**
     * Add a category of dynamic generated lang entries for the {@link LangMerger}.
     * @param display the display header for the category
     * @param provider the provider for the lang entries
     * @param preTask the task to run before fetching lang entries from the provider
     * @return self
     */
    public LangFactory gen(String display, Supplier<JsonObject> provider, Runnable preTask) {
        langMerger.partials.add(new LangPartial.Gen(modid, display, provider, preTask));
        return this;
    }
    
    /**
     * Merge a file from {@code assets/modid/lang/default/} folder to merge into the {@link LangMerger}.
     * @param filename the filename, without extension (as it should always be ".json")
     * @param display the display header for the category
     * @return self
     */
    public LangFactory merge(String filename, String display) {
        langMerger.partials.add(new LangPartial.Merge(modid, display, filename));
        return this;
    }
    
    /**
     * Add ponder localization generator for the {@link LangMerger}.
     * @param preTask the task to run before fetching lang entries from the provider,
     *                   in most cases you should force bootstrap your ponders here.
     * @return self
     */
    // FIXME :: record() is deprecated and will be removed
    public LangFactory ponders(Runnable preTask) {
        langMerger.partials.add(new LangPartial.Gen(
            modid,
            "Ponder Content",
            () -> {
                var ret = new JsonObject();
                PonderLocalization.generateSceneLang();
                PonderLocalization.record(modid, ret);
                return ret;
            },
            preTask)
        );
        return this;
    }
    
    /**
     * Add advancement localization generator for the {@link LangMerger}. <br>
     * Should only be used with {@link AdvancementHolder}.
     * @param preTask the task to run before fetching lang entries from the provider,
     *                   in most cases you should force bootstrap your advancements here.
     * @return self
     */
    public LangFactory advancements(Runnable preTask) {
        langMerger.partials.add(new LangPartial.Gen(
            modid,
            "Advancements",
            () -> AdvancementHolder.provideLangEntries(modid),
            preTask)
        );
        return this;
    }
    
    /**
     * Merge {@code assets/modid/lang/default/tooltips.json} into the {@link LangMerger}. <br>
     * See Create's tooltips.json for how to create your Create-styled tooltip localization.
     * @return self
     */
    public LangFactory tooltips() {
        langMerger.partials.add(new LangPartial.Merge(modid, "Item Descriptions", "tooltips"));
        return this;
    }
    
    /**
     * Merge {@code assets/modid/lang/default/interface.json} into the {@link LangMerger}.
     * @return self
     */
    public LangFactory ui() {
        langMerger.partials.add(new LangPartial.Merge(modid, "UI & Messages", "interface"));
        return this;
    }
    
    /**
     * Define keys that should not be transfer to the foreign lang file templates. <br>
     * @param entries
     * @return self
     * @see com.simibubi.create.foundation.data.LangMerger#populateLangIgnore()
     */
    public LangFactory ignore(String... entries) {
        Collections.addAll(langMerger.ignore, entries);
        return this;
    }

}
