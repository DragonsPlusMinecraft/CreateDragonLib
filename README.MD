# Features
## Language File Datagen & Merger
All game elements, advancements and p support language file datagen.
> Lang datagen works properly only if:   
> **Game Elements** must be registered with **Registrate**.  
> **Advancements** must use **our ModAdvancementFactory** to build advancement.  
> **Ponders** must use **Ponder-related-method of Create itself**.

Also we allows you to merge any existing lang file into lang datagen:

### Example:
In constructor of entrance class of your mod:
```java
    public ExampleMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        doneRegisterWork();

        // Lang datagen for Game Elements is enabled and cannot be disabled.
        AutoLang autoLang = AutoLang.create(MOD_NAME,MOD_ID)
                // Enable Lang datagen for Ponders
                .enableForPonders()
                // Enable Lang datagen for Advancements
                .enableForAdvancement()
                // Merge /lang 下任意位置的 interface.json into Lang datagen. Create uses this path for gui text.
                .mergeCreateStyleInterfaceLang()
                // Merge /lang 下任意位置的 tooltips.json into Lang datagen. Create uses this path for tooltips text.
                .mergeCreateStyleTooltipLang()
                // Merge any lang tile you want
                // This will Merge /lang 下任意位置的 tooltips.json into Lang datagen.
                .merge("fool","JUST A EXAMPLE")
                // enable custom Lang Partial
                .enable("auto-fool","JUST A EXAMPLE",AnyProvider::provideJsonObject);
                

        modEventBus.addListener(autoLang::init);
    }
```



## Create-ish Advancements & Accumulative Trigger
### Advancement Creation
```java
public class AllAdvancments {
    public static final ModAdvancementFactory factory = ModAdvancementFactory.of(MOD_ID);
    public static final ModAdvancement EXAMPLE_ADVANCEMENT = factory.builder("example_advancement")
            .title("Example Advancement")
            .description("Just an example advancement.")
            .icon(AllItems.SOMETHING)
            .externalTrigger("an_external_trigger", anExternalTrigger)
            .announce(true)
            .frame(FrameType.CHALLENGE)
            .parent(OTHER_ADVANCEMENT)
            .build();
    
    public static void register(){}
}
```
Then send `register` method into `FMLCommonSetupEvent#enqueueWork`.

### Accumulative Trigger
Just like its name, it accumulates trigger time and when total triggered time reaches standard, related criterion is fulfilled.  
Use `ModTriggerFactory`and `AccumulativeTrigger`.

# Utilities & APIs
## Register
### RegistrateHelper
Create has fixed namespace/modid in some registration methods, we provide alternative versions of these methods.
### TagFactory
Tag Creation and Datagen.
>You must load all tags first before you `registerDatagen`.

## Event
### FillCreateItemGroupEvent
Provides the ability to insert items into Create's Creative Tab via this event.
### FluidLavaInteractionRegisterEvent
Customize the reaction of other liquids when they encounter lava.

## GUI
### ComponentLabel
An extension for Label which renders Component correctly.
### GuiTexture
A simple ScreenElement render picture.

## Lang
### ModLangBuilder
LangBuilder with your own namespace/modid, and 'forGoggles' method from LangBuilder.
### ModLang
ModLangBuilder utility methods, still with your own namespace/modid.