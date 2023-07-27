package plus.dragons.createdragonlib.mixin;

import com.simibubi.create.AllCreativeModeTabs;;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import plus.dragons.createdragonlib.init.FillCreateItemGroupEvent;

import java.util.List;
import java.util.function.Function;

@Mixin(AllCreativeModeTabs.RegistrateDisplayItemsGenerator.class)
public abstract class CreateItemGroupBaseMixin implements CreativeModeTab.DisplayItemsGenerator {

    @Shadow(remap = false)
    @Final
    private boolean mainTab;

    @Shadow(remap = false)
    private static void outputAll(CreativeModeTab.Output output, List<Item> items, Function<Item, ItemStack> stackFunc, Function<Item, CreativeModeTab.TabVisibility> visibilityFunc) {
        throw new AssertionError();
    }

    @Redirect(method = "accept",
            at = @At(value = "INVOKE",
                    target = "Lcom/simibubi/create/AllCreativeModeTabs$RegistrateDisplayItemsGenerator;outputAll(Lnet/minecraft/world/item/CreativeModeTab$Output;Ljava/util/List;Ljava/util/function/Function;Ljava/util/function/Function;)V"),
            remap = false)
    private void injectOutput(CreativeModeTab.Output item, List<Item> output, Function<Item, ItemStack> items, Function<Item, CreativeModeTab.TabVisibility> stackFunc) {
        var event = new FillCreateItemGroupEvent(mainTab, output);
        MinecraftForge.EVENT_BUS.post(event);
        event.apply();
        outputAll(item,output,items,stackFunc);
    }
    
}
