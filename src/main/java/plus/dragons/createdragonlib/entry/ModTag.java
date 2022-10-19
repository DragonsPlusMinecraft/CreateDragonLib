package plus.dragons.createdragonlib.entry;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.Locale;
import java.util.Objects;

public interface ModTag<T, P extends RegistrateTagsProvider<T>> {
    ITagManager<net.minecraft.world.level.block.Block> BLOCK_TAGS = Objects.requireNonNull(ForgeRegistries.BLOCKS.tags());
    ITagManager<net.minecraft.world.item.Item> ITEM_TAGS = Objects.requireNonNull(ForgeRegistries.ITEMS.tags());
    ITagManager<net.minecraft.world.level.material.Fluid> FLUID_TAGS = Objects.requireNonNull(ForgeRegistries.FLUIDS.tags());
    String FORGE = "forge";
    String CREATE = "create";
    
    TagKey<T> tag();
    
    boolean hasDatagen();
    
    default void datagen(P pov) {
        //NO-OP
    }
    
    static String toTagName(String enumName) {
        return enumName.replace('$', '/').toLowerCase(Locale.ROOT);
    }
    
    static <T extends net.minecraft.world.level.block.Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOrPickaxe() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_AXE).tag(BlockTags.MINEABLE_WITH_PICKAXE);
    }
    
    static <T extends net.minecraft.world.level.block.Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOnly() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_AXE);
    }
    
    static <T extends net.minecraft.world.level.block.Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> pickaxeOnly() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_PICKAXE);
    }
    
    static <T extends net.minecraft.world.level.block.Block, P> NonNullFunction<BlockBuilder<T, P>, ItemBuilder<BlockItem, BlockBuilder<T, P>>> tagBlockAndItem(String namespace, String... paths) {
        return block -> {
            ItemBuilder<BlockItem, BlockBuilder<T, P>> item = block.item();
            for (String path : paths) {
                block.tag(BLOCK_TAGS.createTagKey(new ResourceLocation(namespace, path)));
                item.tag(ITEM_TAGS.createTagKey(new ResourceLocation(namespace, path)));
            }
            return item;
        };
    }
    
    class Block implements ModTag<net.minecraft.world.level.block.Block, RegistrateTagsProvider<net.minecraft.world.level.block.Block>> {
        
        final TagKey<net.minecraft.world.level.block.Block> tag;
        final boolean datagen;
        
        Block(String namespace, String name, boolean datagen) {
            this.tag = BLOCK_TAGS.createTagKey(new ResourceLocation(namespace, name));
            this.datagen = datagen;
        }

    
        @Override
        public TagKey<net.minecraft.world.level.block.Block> tag() {
            return tag;
        }
    
        @Override
        public boolean hasDatagen() {
            return datagen;
        }
    }

    class Item implements ModTag<net.minecraft.world.item.Item, RegistrateItemTagsProvider> {
        
        final TagKey<net.minecraft.world.item.Item> tag;
        final boolean datagen;
    
        Item(String namespace, String name, boolean datagen) {
            this.tag = ITEM_TAGS.createTagKey(new ResourceLocation(namespace, name));
            this.datagen = datagen;
        }
    
        @Override
        public TagKey<net.minecraft.world.item.Item> tag() {
            return tag;
        }
    
        @Override
        public boolean hasDatagen() {
            return datagen;
        }
    }

    class Fluid implements ModTag<net.minecraft.world.level.material.Fluid, RegistrateTagsProvider<net.minecraft.world.level.material.Fluid>> {
        
        final TagKey<net.minecraft.world.level.material.Fluid> tag;
        final boolean datagen;
    
        Fluid(String namespace, String name, boolean datagen) {
            this.tag = FLUID_TAGS.createTagKey(new ResourceLocation(namespace, name));
            this.datagen = datagen;
        }
    
        @Override
        public TagKey<net.minecraft.world.level.material.Fluid> tag() {
            return tag;
        }
    
        @Override
        public boolean hasDatagen() {
            return datagen;
        }
    }
    
}
