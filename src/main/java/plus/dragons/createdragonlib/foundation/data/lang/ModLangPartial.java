package plus.dragons.createdragonlib.foundation.data.lang;

import com.google.common.base.Supplier;
import com.google.gson.JsonElement;
import com.simibubi.create.foundation.utility.FilesHelper;

public class ModLangPartial {
    private final String namespace;
    private final String display;
    private final String filename;
    private final Supplier<JsonElement> provider;

    ModLangPartial(String namespace, String filename, String display) {
        this.namespace = namespace;
        this.filename = filename;
        this.display = display;
        this.provider = () -> this.fromResource(namespace);
    }

    ModLangPartial(String namespace, String filename, String display, Supplier<JsonElement> customProvider) {
        this.namespace = namespace;
        this.filename = filename;
        this.display = display;
        this.provider = customProvider;
    }

    public String getDisplay() {
        return display;
    }

    public JsonElement provide() {
        return provider.get();
    }

    private JsonElement fromResource(String namespace) {
        String filepath = "assets/" + namespace + "/lang/partial/" + filename + ".json";
        JsonElement element = FilesHelper.loadJsonResource(filepath);
        if (element == null)
            throw new IllegalStateException(String.format("Could not find default lang file: %s", filepath));
        return element;
    }
}
