package plus.dragons.createdragonlib.utility;

import com.simibubi.create.foundation.utility.LangBuilder;
import joptsimple.internal.Strings;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class ModLangBuilder extends LangBuilder {
    public ModLangBuilder(String namespace) {
        super(namespace);
    }

    public void forGoggles(String namespace, List<? super MutableComponent> tooltip, int indents) {
        tooltip.add(ModLang.builder(namespace)
                .text(Strings.repeat(' ', 4 + indents))
                .add(this)
                .component());
    }
}
