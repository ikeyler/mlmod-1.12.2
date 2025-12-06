package ikeyler.mlmod.variables;

import ikeyler.mlmod.itemeditor.ItemEditor;
import ikeyler.mlmod.util.ItemUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class Variable {

    private final VariableType type;
    private final String name;
    public Variable(VariableType type, String name) {
        this.type = type;
        this.name = name;
    }

    public VariableType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public static Variable fromString(String s) {
        String[] split = s.split("::", 2);
        if (!s.contains("::") || split.length < 2) return null;
        VariableType type = Arrays.stream(VariableType.values()).filter(t -> split[0].toUpperCase().equals(t.name())).findFirst().orElse(null);
        if (type == null) return null;
        return new Variable(type, split[1]);
    }

    public static Variable fromItem(ItemStack item) {
        String itemId = item.getItem().getRegistryName().getResourcePath();
        VariableType type = null;
        switch (itemId) {
            case "book": type = VariableType.TEXT; break;
            case "slime_ball": type = VariableType.NUMBER; break;
            case "paper": type = VariableType.LOCATION; break;
            case "magma_cream":
                String itemLore = ItemEditor.getLore(item).toString();
                type = (itemLore.contains("§dСОХРАНЕНО") || itemLore.contains("§dSAVED")) ? VariableType.VAR_SAVED : VariableType.VAR_UNSAVED;
                break;
            case "item_frame":
                type = item.getDisplayName().contains("⎘") ? VariableType.ARRAY_CONST : VariableType.ARRAY_TEMP;
                break;
            case "shulker_shell": type = VariableType.COMPONENT; break;
            case "prismarine_shard": type = VariableType.VECTOR; break;
            default: break;
        }
        if (type == null) return null;
        return new Variable(type, item.getDisplayName());
    }

    public static ItemStack itemFromVariable(Variable variable) {
        ItemStack item = null;
        switch (variable.getType()) { // ignoring these warnings
            case TEXT: item = Item.getByNameOrId("book").getDefaultInstance(); break;
            case NUMBER: item = Item.getByNameOrId("slime_ball").getDefaultInstance(); break;
            case LOCATION: item = Item.getByNameOrId("paper").getDefaultInstance(); break;
            case VAR_UNSAVED: item = ItemUtil.getDynamicVar(false); break;
            case VAR_SAVED: item = ItemUtil.getDynamicVar(true); break;
            case ARRAY_CONST:
            case ARRAY_TEMP: item = Item.getByNameOrId("item_frame").getDefaultInstance(); break;
            case COMPONENT: item = Item.getByNameOrId("shulker_shell").getDefaultInstance(); break;
            case VECTOR: item = Item.getByNameOrId("prismarine_shard").getDefaultInstance(); break;
            default: break;
        }
        item.setStackDisplayName(variable.getName());
        return item;
    }

    public enum VariableType {
        TEXT("mlmod.var.text"),
        NUMBER("mlmod.var.number"),
        LOCATION("mlmod.var.location"),
        VAR_UNSAVED("mlmod.var.var_unsaved"),
        VAR_SAVED("mlmod.var.var_saved"),
        ARRAY_CONST("mlmod.var.array_const"),
        ARRAY_TEMP("mlmod.var.array_temp"),
        COMPONENT("mlmod.var.component"),
        VECTOR("mlmod.var.vector");
        private final String translation;
        VariableType(String translation) {
            this.translation = translation;
        }
        public String getTranslation() {
            return translation;
        }
    }
}
