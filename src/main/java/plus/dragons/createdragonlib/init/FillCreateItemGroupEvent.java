package plus.dragons.createdragonlib.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * Event to add items to Create's creative mode tab.
 */
public class FillCreateItemGroupEvent extends Event {
    private final boolean base;
    private final Map<Item, List<ItemLike>> insertions = new IdentityHashMap<>();
    private final List<Item> output;
    
    public FillCreateItemGroupEvent(boolean base, List<Item> output) {
        this.base = base;
        this.output = output;
    }
    
    /**
     * Get the creative mod tab, so you could determine which tab are you adding into.
     * @return the creative mod tab
     */
    public boolean isBase() {
        return base;
    }
    
    /**
     * Add an {@link ItemStack} after an {@link Item}, should only target Create's existing items in the tab.
     * @param target the item to target
     * @param add the item to add
     */
    public void addInsertion(ItemLike target, ItemLike add) {
        insertions.computeIfAbsent(target.asItem(), $ -> new ArrayList<>()).add(add);
    }
    
    /**
     * Add some {@link ItemStack}s after an {@link Item}, should only target Create's existing items in the tab.
     * @param target the item to target
     * @param adds the items to add
     */
    public void addInsertions(ItemLike target, Collection<ItemLike> adds) {
        insertions.computeIfAbsent(target.asItem(), $ -> new ArrayList<>()).addAll(adds);
    }
    
    @ApiStatus.Internal
    public void apply() {
        ListIterator<Item> it = output.listIterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (insertions.containsKey(item)) {
                for (var inserted : insertions.get(item)) {
                    it.add(inserted.asItem());
                }
                insertions.remove(item);
            }
        }
    }
    
}
