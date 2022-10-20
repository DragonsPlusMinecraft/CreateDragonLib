package plus.dragons.createdragonlib.lang;

import com.google.common.base.Supplier;
import com.google.gson.JsonElement;
import com.simibubi.create.foundation.utility.FilesHelper;

public abstract class ModLangPartial {
    protected final String namespace;
    protected final String display;
    protected final Supplier<JsonElement> provider;

    ModLangPartial(String namespace, String display, Supplier<JsonElement> provider) {
        this.namespace = namespace;
        this.display = display;
        this.provider = provider;
    }

    public String getDisplay() {
        return display;
    }

    public JsonElement provide() {
        return provider.get();
    }

    public static class Merge extends ModLangPartial{
        private final String filename;

        Merge(String namespace, String filename, String display) {
            super(namespace,display, createBox(namespace,filename));
            this.filename = filename;
        }

        static Supplier<JsonElement> createBox(String namespace,String filename){
            var temp = new Box(namespace,filename);
            return temp::fromResource;
        }

        record Box(String namespace,String filename){
            private JsonElement fromResource() {
                String filepath = "assets/" + namespace + "/lang/partial/" + filename + ".json";
                JsonElement element = FilesHelper.loadJsonResource(filepath);
                if (element == null)
                    throw new IllegalStateException(String.format("Could not find default lang file: %s", filepath));
                return element;
            }
        }
    }

    public static class Gen extends ModLangPartial{

        private final Runnable preTask;

        Gen(String namespace, String display, Supplier<JsonElement> customProvider, Runnable preTask) {
            super(namespace,display,customProvider);
            this.preTask = preTask;
        }

        @Override
        public JsonElement provide() {
            preTask.run();
            return super.provide();
        }
    }
}
