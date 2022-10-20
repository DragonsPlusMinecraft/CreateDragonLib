package plus.dragons.createdragonlib.advancement;

import com.google.gson.JsonObject;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.utility.Components;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonlib.mixin.CreateAdvancementConstructor;

import java.util.*;
import java.util.function.Consumer;

public class ModAdvancement {

    public static final Map<String, List<ModAdvancement>> ENTRIES_MAP = new HashMap<>();
    protected final ResourceLocation id;
    protected final Advancement.Builder builder;
    @Nullable
    protected final SimpleTrigger builtinTrigger;
    protected final String titleKey;
    protected final String descriptionKey;
    protected final String title;
    protected final String description;
    @Nullable
    protected final ModAdvancement parent;
    @Nullable
    protected final CreateAdvancement createAdvancement;
    protected Advancement advancement;
    
    protected ModAdvancement(String namespace,String id, Advancement.Builder builder, @Nullable ModAdvancement parent, boolean builtin, String title, String description) {
        this.id = new ResourceLocation(namespace,id);
        this.builder = builder;
        this.parent = parent;
        if(builtin) {
            this.builtinTrigger = ModTriggerFactory.addSimple(new ResourceLocation(namespace,"builtin/" + id));
            this.builder.addCriterion("builtin", builtinTrigger.instance());
        } else this.builtinTrigger = null;
        this.createAdvancement = CreateAdvancementConstructor.createInstance(id, $ -> $);
        ((ModdedCreateAdvancement) createAdvancement).fromModAdvancement(this);
        this.titleKey = new StringJoiner(".").add("advancement").add(namespace).add(id).toString();
        this.descriptionKey = titleKey + ".desc";this.title = title;
        this.description = description;
    }
    
    public ResourceLocation id() {
        return id;
    }
    
    public String titleKey() {
        return titleKey;
    }
    
    public String descriptionKey() {
        return descriptionKey;
    }
    
    public String title() {
        return title;
    }
    
    public String description() {
        return description;
    }
    
    @Nullable
    public SimpleTrigger getTrigger() {
        return builtinTrigger;
    }
    
    public CreateAdvancement asCreateAdvancement() {
        if(createAdvancement == null)
            throw new UnsupportedOperationException("Advancement [" + id + "] can not convert into CreateAdvancement!");
        return createAdvancement;
    }
    
    public boolean isAlreadyAwardedTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return true;
        Advancement advancement = sp.getServer().getAdvancements().getAdvancement(id);
        if (advancement == null)
            return true;
        return sp.getAdvancements().getOrStartProgress(advancement).isDone();
    }
    
    public void awardTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return;
        if (builtinTrigger == null)
            throw new UnsupportedOperationException("Advancement [" + id + "] uses external Triggers, it cannot be awarded directly");
        builtinTrigger.trigger(sp);
    }
    
    public void save(Consumer<Advancement> consumer) {
        if (parent != null) builder.parent(parent.advancement);
        advancement = builder.save(consumer, id.toString());
    }
    
    public void appendToLang(JsonObject object) {
        object.addProperty(titleKey(), title());
        object.addProperty(descriptionKey(), description());
    }

    public static JsonObject provideLangEntries(String namespace) {
        JsonObject object = new JsonObject();
        var advancements = ENTRIES_MAP.get(namespace);
        if(advancements==null) return object;
        for (var advancement : advancements) {
            advancement.appendToLang(object);
        }
        return object;
    }

    public static class Builder {
        private final String namespace;
        @Nullable
        private final ResourceLocation background;
        private final String id;
        private final Advancement.Builder builder = Advancement.Builder.advancement();
        @Nullable
        private ModAdvancement parent;
        private boolean builtin = true;
        private String title = "Untitled";
        private String description = "No Description";
        private ItemStack icon = ItemStack.EMPTY;
        private FrameType frame = FrameType.TASK;
        private boolean toast = true;
        private boolean announce = false;
        private boolean hide = false;

        public Builder(String namespace,String id) {
            this.namespace = namespace;
            this.id = id;
            this.background = "root".equals(id) ? new ResourceLocation(namespace,"textures/gui/advancements.png") : null;
        }
    
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder icon(ItemStack stack) {
            this.icon = stack;
            return this;
        }

        public Builder icon(ItemProviderEntry<?> item) {
            return icon(item.asStack());
        }

        public Builder icon(ItemLike item) {
            return icon(new ItemStack(item));
        }

        public Builder frame(FrameType frame) {
            this.frame = frame;
            return this;
        }

        public Builder toast(boolean bl) {
            this.toast = bl;
            return this;
        }

        public Builder announce(boolean bl) {
            this.announce = bl;
            return this;
        }

        public Builder hidden() {
            this.hide = true;
            return this;
        }

        public Builder externalTrigger(String key, CriterionTriggerInstance trigger) {
            builder.addCriterion(key, trigger);
            this.builtin = false;
            return this;
        }

        public Builder parent(ResourceLocation id) {
            builder.parent(new Advancement(id, null, null, AdvancementRewards.EMPTY, Map.of(), new String[0][0]));
            return this;
        }

        public Builder parent(ModAdvancement advancement) {
            this.parent = advancement;
            return this;
        }

        public Builder transform(NonNullUnaryOperator<Advancement.Builder> transform) {
            transform.apply(builder);
            return this;
        }

        public ModAdvancement build() {
            if (hide) description += "\u00A77\n(Hidden Advancement)";
            ModAdvancement advancement = new ModAdvancement(namespace,id, builder, parent, builtin, title, description);
            builder.display(
                icon,
                Components.translatable(advancement.titleKey),
                Components.translatable(advancement.descriptionKey).withStyle(s -> s.withColor(0xDBA213)),
                background,
                frame,
                toast,
                announce,
                hide
            );
            if(!ENTRIES_MAP.containsKey(namespace)) ENTRIES_MAP.put(namespace,new ArrayList<>());
            ENTRIES_MAP.get(namespace).add(advancement);
            return advancement;
        }
        
    }
    
}
