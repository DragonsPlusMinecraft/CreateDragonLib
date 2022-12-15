package plus.dragons.createdragonlib.lang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.utility.FilesHelper;

import java.util.function.Supplier;

abstract class LangPartial {
    protected final String modid;
    protected final String display;

    LangPartial(String modid, String display) {
        this.modid = modid;
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public abstract JsonObject provide();

    static class Merge extends LangPartial {
        private final String filename;

        Merge(String modid, String display, String filename) {
            super(modid, display);
            this.filename = filename;
        }
    
        @Override
        public JsonObject provide() {
            String filepath = "assets/" + modid + "/lang/default/" + filename + ".json";
            JsonElement element = FilesHelper.loadJsonResource(filepath);
            if (element == null)
                throw new IllegalStateException(String.format("Could not find default lang file: %s", filepath));
            return element.getAsJsonObject();
        }
        
    }

    static class Gen extends LangPartial {
        private final Supplier<JsonObject> provider;
        private final Runnable preTask;

        Gen(String modid, String display, Supplier<JsonObject> provider, Runnable preTask) {
            super(modid, display);
            this.provider = provider;
            this.preTask = preTask;
        }

        @Override
        public JsonObject provide() {
            preTask.run();
            return provider.get();
        }
        
    }
    
}
