package simplexity.simplebucketmobs.listener;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public class CaptureItem {

    private final String itemName;
    private final boolean enchantGlint;
    private final NamespacedKey itemModel;
    private final Material itemType;
    private final Integer stackSize;


    public CaptureItem(String itemName, boolean enchantGlint, NamespacedKey itemModel, Material itemType, Integer stackSize) {
        this.itemName = itemName;
        this.enchantGlint = enchantGlint;
        this.itemModel = itemModel;
        this.itemType = itemType;
        this.stackSize = stackSize;
    }


    public String getItemName() {
        return itemName;
    }

    public boolean isEnchantGlint() {
        return enchantGlint;
    }

    public NamespacedKey getItemModel() {
        return itemModel;
    }

    public Material getItemType() {
        return itemType;
    }

    public Integer getStackSize() {
        return stackSize;
    }
}
