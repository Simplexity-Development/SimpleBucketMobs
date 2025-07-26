package simplexity.simplebucketmobs.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import simplexity.simplebucketmobs.SimpleBucketMobs;
import simplexity.simplebucketmobs.config.Config;
import simplexity.simplebucketmobs.config.Texture;

import java.util.HashMap;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public class BucketHandler {

    private static final MiniMessage miniMessage = SimpleBucketMobs.getMiniMessage();

    public static ItemStack getMobBucket(LivingEntity entity) {
        ItemStack bucketStack = new ItemStack(Material.BUCKET);
        bucketStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        Component nameComponent = getBucketName(entity);
        bucketStack.setData(DataComponentTypes.CUSTOM_NAME, nameComponent);
        bucketStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        byte[] serializedEntity = Bukkit.getUnsafe().serializeEntity(entity);
        if (Config.getInstance().isUseResourcePack()) {
            String itemModelString = Texture.getInstance().getItemModel(entity);

            if (itemModelString == null || !itemModelString.contains(":")) {
                Bukkit.getLogger().warning("Invalid or missing item model string for entity: " + entity.getType() +
                                           ". Value: '" + itemModelString + "'");
            } else {
                String[] split = itemModelString.trim().split(":");

                if (split.length != 2 || split[0].isEmpty() || split[1].isEmpty()) {
                    Bukkit.getLogger().warning("Malformed item model string for entity: " + entity.getType() +
                                               ". Value: '" + itemModelString + "'");
                } else {
                    NamespacedKey namespacedKey = new NamespacedKey(split[0], split[1]);
                    bucketStack.setData(DataComponentTypes.ITEM_MODEL, namespacedKey);
                }
            }
        }
        bucketStack.editPersistentDataContainer(pdc -> pdc.set(BucketMob.newMobTag, PersistentDataType.BYTE_ARRAY, serializedEntity));
        return bucketStack;
    }


    public static void addMobBucketToInventory(Player player, ItemStack bucket) {
        Inventory inventory = player.getInventory();
        int amountOfBuckets = player.getInventory().getItemInMainHand().getAmount();
        player.getInventory().getItemInMainHand().setAmount(amountOfBuckets - 1);
        HashMap<Integer, ItemStack> leftoverItems = inventory.addItem(bucket);
        if (leftoverItems.isEmpty()) return;
        for (ItemStack leftover : leftoverItems.values()) {
            player.getLocation().getWorld().dropItem(player.getLocation(), leftover);
        }
    }

    private static Component getBucketName(LivingEntity entity) {
        Component entityName = entity.customName();
        if (entityName == null) entityName = entity.name();
        String typeName = entity.getType().name();
        String typeNameCased = nameCase(typeName);
        String configName = Config.getInstance().getBucketTitle();
        return miniMessage.deserialize(configName,
                Placeholder.component("display_name", entityName),
                Placeholder.parsed("type", entity.getType().name()),
                Placeholder.parsed("type_name_cased", typeNameCased));
    }

    /**
     * Converts a string to Name Case
     *
     * @param input String
     * @return String but using Name Case
     * @implNote Thanks Baeldung (https://www.baeldung.com/java-string-title-case)
     */
    private static String nameCase(String input) {
        if (input == null || input.isBlank()) return input;
        input = input.replace('_', ' ');
        StringBuilder nameCased = new StringBuilder();
        boolean toUpper = true;
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) toUpper = true;
            else if (toUpper) {
                c = Character.toTitleCase(c);
                toUpper = false;
            } else {
                c = Character.toLowerCase(c);
            }
            nameCased.append(c);
        }
        return nameCased.toString();
    }

}
