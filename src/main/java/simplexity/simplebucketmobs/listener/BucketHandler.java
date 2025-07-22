package simplexity.simplebucketmobs.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public class BucketHandler {

    public static ItemStack getMobBucket(LivingEntity entity) {
        ItemStack bucketStack = new ItemStack(Material.BUCKET);
        bucketStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        Component nameComponent = getBucketName(entity);
        bucketStack.setData(DataComponentTypes.CUSTOM_NAME, nameComponent);
        bucketStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        byte[] serializedEntity = Bukkit.getUnsafe().serializeEntity(entity);
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
        Component nameComponent = Component.empty();
        nameComponent = nameComponent.append(Component.text(entity.getType().toString().toLowerCase()));

        nameComponent = nameComponent.append(Component.text(" Bucket"));
        return nameComponent;
    }

}
