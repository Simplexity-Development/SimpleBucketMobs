package simplexity.simplebucketmobs.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import simplexity.simplebucketmobs.SimpleBucketMobs;
import simplexity.simplebucketmobs.config.ConfigHandler;
import simplexity.simplebucketmobs.config.Texture;

import java.util.HashMap;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public class BucketHandler {

    private static final MiniMessage miniMessage = SimpleBucketMobs.getMiniMessage();

    public static ItemStack getMobBucket(LivingEntity entity) {
        ItemStack bucketStack = new ItemStack(ConfigHandler.getInstance().getMobBucketMaterial());
        bucketStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        Component nameComponent = getBucketName(entity);
        bucketStack.setData(DataComponentTypes.CUSTOM_NAME, nameComponent);
        bucketStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, ConfigHandler.getInstance().isEnchantmentGlint());
        byte[] serializedEntity = Bukkit.getUnsafe().serializeEntity(entity);
        if (ConfigHandler.getInstance().isUsingResourcePack()) {
            NamespacedKey modelKey = Texture.getInstance().getItemModel(entity);
            bucketStack.setData(DataComponentTypes.ITEM_MODEL, modelKey);
        }
        bucketStack.editPersistentDataContainer(pdc -> pdc.set(InteractListeners.newMobTag, PersistentDataType.BYTE_ARRAY, serializedEntity));
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
        String configName = ConfigHandler.getInstance().getBucketTitle();
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
