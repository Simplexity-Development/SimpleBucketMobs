package simplexity.simplebucketmobs.listener;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public class CapturedItem {
    public final String itemName;
    public final String itemLore;
    public final boolean enchantGlint;
    public final NamespacedKey itemModel;
    public final Material itemType;

    public CapturedItem(String itemName, String itemLore, boolean enchantGlint, NamespacedKey itemModel, Material itemType) {
        this.itemName = itemName;
        this.itemLore = itemLore;
        this.enchantGlint = enchantGlint;
        this.itemModel = itemModel;
        this.itemType = itemType;
    }
}