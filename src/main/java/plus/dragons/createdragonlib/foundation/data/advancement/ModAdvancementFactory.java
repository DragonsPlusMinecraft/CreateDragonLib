package plus.dragons.createdragonlib.foundation.data.advancement;

public class ModAdvancementFactory {
    private final String namespace;

    public static ModAdvancementFactory create(String namespace){
        return new ModAdvancementFactory(namespace);
    }

    ModAdvancementFactory(String namespace) {
        this.namespace = namespace;
    }

    public ModAdvancement.Builder builder(String id) {
        return new ModAdvancement.Builder(namespace,id);
    }
}
