package plus.dragons.createdragonlib.entry;

import com.simibubi.create.AllFluids;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.Lang;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;

public class RegistrateHelper {
    public static class EntityType{
        public static <T extends net.minecraft.world.entity.Entity> CreateEntityBuilder<T, ?> register(CreateRegistrate registrate, String name, net.minecraft.world.entity.EntityType.EntityFactory<T> factory,
                                                                                                        NonNullSupplier<NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>>> renderer,
                                                                                                        MobCategory group, int range, int updateFrequency, boolean sendVelocity, boolean immuneToFire,
                                                                                                        NonNullConsumer<net.minecraft.world.entity.EntityType.Builder<T>> propertyBuilder) {
            String id = Lang.asId(name);
            return (CreateEntityBuilder<T, ?>) registrate
                    .entity(id, factory, group)
                    .properties(b -> b.setTrackingRange(range)
                            .setUpdateInterval(updateFrequency)
                            .setShouldReceiveVelocityUpdates(sendVelocity))
                    .properties(propertyBuilder)
                    .properties(b -> {
                        if (immuneToFire)
                            b.fireImmune();
                    })
                    .renderer(renderer);
        }

    }


    public static class ContainerType{
        public static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(CreateRegistrate registrate,
                String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
            return registrate.menu(name, factory, screenFactory).register();
        }
    }

    public static class Fluid{
        /**
         * Removing alpha from tint prevents optifine from forcibly applying biome
         * colors to modded fluids (Makes translucent fluids disappear)
         */
        public static class NoColorFluidAttributes extends AllFluids.TintedFluidType {

            public NoColorFluidAttributes(Properties properties, ResourceLocation stillTexture,
                                          ResourceLocation flowingTexture) {
                super(properties, stillTexture, flowingTexture);
            }

            @Override
            protected int getTintColor(FluidStack stack) {
                return NO_TINT;
            }

            @Override
            public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
                return 0x00ffffff;
            }

        }
    }
}
