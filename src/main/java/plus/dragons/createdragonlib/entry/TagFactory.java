package plus.dragons.createdragonlib.entry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.ProviderType;

import java.util.ArrayList;
import java.util.List;

public class TagFactory {
    private final CreateRegistrate registrate;
    private final List<ModTag.Block> blockTags;
    private final List<ModTag.Item> itemTags;
    private final List<ModTag.Fluid> fluidTags;

    public TagFactory(CreateRegistrate registrate) {
        this.registrate = registrate;
        this.blockTags = new ArrayList<>();
        this.itemTags = new ArrayList<>();
        this.fluidTags = new ArrayList<>();
    }

    public TagFactory create(CreateRegistrate registrate){
        return new TagFactory(registrate);
    }

    public ModTag.Block block(String name, boolean datagen){
        var ret = new ModTag.Block(registrate.getModid(),name,datagen);
        blockTags.add(ret);
        return ret;
    }

    public ModTag.Item item(String name, boolean datagen){
        var ret = new ModTag.Item(registrate.getModid(),name,datagen);
        itemTags.add(ret);
        return ret;
    }

    public ModTag.Fluid fluid(String name, boolean datagen){
        var ret = new ModTag.Fluid(registrate.getModid(),name,datagen);
        fluidTags.add(ret);
        return ret;
    }

   public void registerDatagen() {
       blockTags.stream()
                .filter(ModTag::hasDatagen)
                .forEach(tag -> registrate.addDataGenerator(ProviderType.BLOCK_TAGS, tag::datagen));
       itemTags.stream()
                .filter(ModTag::hasDatagen)
                .forEach(tag -> registrate.addDataGenerator(ProviderType.ITEM_TAGS, tag::datagen));
       fluidTags.stream()
                .filter(ModTag::hasDatagen)
                .forEach(tag -> registrate.addDataGenerator(ProviderType.FLUID_TAGS, tag::datagen));
    }
}
