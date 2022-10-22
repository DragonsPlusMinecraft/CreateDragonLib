package plus.dragons.createdragonlib.lang;

import com.google.common.base.Supplier;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import plus.dragons.createdragonlib.advancement.Advancement;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AutoLang {
    private Function<String, LangPartial.Merge> INTERFACE = (namespace) -> new LangPartial.Merge(namespace, "interface","UI & Messages");
    private Function<String, LangPartial.Merge> TOOLTIPS = (namespace) -> new LangPartial.Merge(namespace, "tooltips","Item Descriptions");
    private BiFunction<String,Runnable, LangPartial.Gen> PONDERS = (namespace, preTask) -> new LangPartial.Gen(namespace, "Ponder Content", () -> {
        var ret = new JsonObject();
        PonderLocalization.generateSceneLang();
        PonderLocalization.record(namespace,ret);
        return ret;
    }, preTask);
    private BiFunction<String,Runnable, LangPartial.Gen> ADVANCEMENTS = (namespace, preTask) -> new LangPartial.Gen(namespace, "Advancements", () -> Advancement.provideLangEntries(namespace),preTask);
    private final String namespace;
    private final LangMerger merger;

    public static AutoLang create(String modName, String namespace){
        return new AutoLang(modName,namespace);
    }

    private AutoLang(String modName, String namespace) {
        this.namespace = namespace;
        this.merger = new LangMerger(modName,namespace);
    }

    public AutoLang enableForPonders(Runnable preTask){
        merger.addPartial(()->PONDERS.apply(namespace,preTask));
        return this;
    }

    public AutoLang enableForAdvancement(Runnable preTask){
        merger.addPartial(()->ADVANCEMENTS.apply(namespace,preTask));
        return this;
    }

    public AutoLang merge(String filename , String display){
        merger.addPartial(()->new LangPartial.Merge(namespace,filename,display));
        return this;
    }

    public AutoLang enable(String filename , String display, Supplier<JsonElement> customProvider, Runnable preTask){
        merger.addPartial(()->new LangPartial.Gen(namespace,filename,customProvider,preTask));
        return this;
    }

    public AutoLang mergeCreateStyleTooltipLang(){
        merger.addPartial(()->TOOLTIPS.apply(namespace));
        return this;
    }

    public AutoLang mergeCreateStyleInterfaceLang(){
        merger.addPartial(()->INTERFACE.apply(namespace));
        return this;
    }

    public void registerDatagen(final GatherDataEvent event) {
        DataGenerator datagen = event.getGenerator();
        merger.addGenerator(datagen);
        datagen.addProvider(true,merger);
    }
}
