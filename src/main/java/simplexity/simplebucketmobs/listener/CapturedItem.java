package simplexity.simplebucketmobs.listener;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public class CapturedItem {

    private final String itemName;
    private final String itemLore;
    private final boolean enchantGlint;
    private final NamespacedKey itemModel;
    private final Material itemType;


    public CapturedItem(String itemName, String itemLore,boolean enchantGlint, NamespacedKey itemModel, Material itemType) {
        this.itemName = itemName;
        this.itemLore = itemLore;
        this.enchantGlint = enchantGlint;
        this.itemModel = itemModel;
        this.itemType = itemType;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemLore() {
        return itemLore;
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
}
