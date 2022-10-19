package plus.dragons.createdragonlib.foundation.data.lang;

import com.google.common.base.Supplier;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import plus.dragons.createdragonlib.foundation.data.advancement.ModAdvancement;
import plus.dragons.createdragonlib.foundation.data.advancement.ModAdvancementGen;

import java.util.function.Function;

public class AutoLang {
    private Function<String, ModLangPartial> INTERFACE = (namespace) -> new ModLangPartial(namespace, "interface","UI & Messages");
    private Function<String, ModLangPartial> TOOLTIPS = (namespace) -> new ModLangPartial(namespace, "tooltips","Item Descriptions");
    private Function<String, ModLangPartial> PONDERS = (namespace) -> new ModLangPartial(namespace, "ponder","Ponder Content", () -> {
        var ret = new JsonObject();
        PonderLocalization.generateSceneLang();
        PonderLocalization.record(namespace,ret);
        return ret;
    });
    private Function<String, ModLangPartial> ADVANCEMENTS = (namespace) -> new ModLangPartial(namespace, "advancement","Advancements", () -> ModAdvancement.provideLangEntries(namespace));
    private final String namespace;
    private final ModLangMerger merger;

    public static AutoLang create(String modName,String namespace){
        return new AutoLang(modName,namespace);
    }

    private AutoLang(String modName, String namespace) {
        this.namespace = namespace;
        this.merger = new ModLangMerger(modName,namespace);
    }

    public AutoLang enableForPonders(){
        merger.addPartial(PONDERS.apply(namespace));
        return this;
    }

    public AutoLang enableForAdvancement(){
        merger.addPartial(ADVANCEMENTS.apply(namespace));
        return this;
    }

    public AutoLang merge(String filename ,String display){
        merger.addPartial(new ModLangPartial(namespace,filename,display));
        return this;
    }

    public AutoLang enable(String filename ,String display, Supplier<JsonElement> customProvider){
        merger.addPartial(new ModLangPartial(namespace,filename,display,customProvider));
        return this;
    }

    public AutoLang mergeCreateStyleTooltipLang(){
        merger.addPartial(TOOLTIPS.apply(namespace));
        return this;
    }

    public AutoLang mergeCreateStyleInterfaceLang(){
        merger.addPartial(INTERFACE.apply(namespace));
        return this;
    }

    public void init(final GatherDataEvent event) {
        DataGenerator datagen = event.getGenerator();
        merger.addGenerator(datagen);
        datagen.addProvider(true,merger);
        datagen.addProvider(true,new ModAdvancementGen(namespace,datagen));
    }
}
