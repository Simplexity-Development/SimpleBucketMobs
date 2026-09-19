package simplexity.simplebucketmobs.listener;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public class CaptureItem {
    public final String itemName;
    public final boolean enchantGlint;
    public final NamespacedKey itemModel;
    public final Material itemType;
    public final Integer stackSize;

    public CaptureItem(String itemName, boolean enchantGlint, NamespacedKey itemModel, Material itemType, Integer stackSize) {
        this.itemName = itemName;
        this.enchantGlint = enchantGlint;
        this.itemModel = itemModel;
        this.itemType = itemType;
        this.stackSize = stackSize;
    }
}