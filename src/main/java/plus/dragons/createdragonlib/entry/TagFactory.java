package plus.dragons.createdragonlib.entry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.tags.BlockTags;

import java.util.ArrayList;
import java.util.List;

public class TagFactory {
    public static <T extends net.minecraft.world.level.block.Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOrPickaxe() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_AXE).tag(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    public static <T extends net.minecraft.world.level.block.Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOnly() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_AXE);
    }

    public static <T extends net.minecraft.world.level.block.Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> pickaxeOnly() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_PICKAXE);
    }
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
