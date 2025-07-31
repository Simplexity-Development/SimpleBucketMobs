package simplexity.simplebucketmobs.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.slf4j.Logger;
import simplexity.simplebucketmobs.SimpleBucketMobs;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public class EntityHandler {

    private static final Logger logger = SimpleBucketMobs.getPlugin().getSLF4JLogger();

    private static final List<ItemStack> pleaseStopDuplicatingMobsThanks = new ArrayList<>();

    public static void handleMobSpawn(Player player, ItemStack itemStack, Block block) {
        byte[] entityBytes = itemStack.getPersistentDataContainer().get(InteractListeners.newMobTag, PersistentDataType.BYTE_ARRAY);
        if (entityBytes == null) return;
        Entity deserializedEntity = Bukkit.getUnsafe().deserializeEntity(entityBytes, block.getWorld(), false, false);
        if (!(deserializedEntity instanceof LivingEntity livingEntity)) {
            logger.warn("Entity saved in this bucket was not a living entity, idk how you managed to do that but it's not gonna spawn. Entity info: {}", deserializedEntity);
            return;
        }
        if (pleaseStopDuplicatingMobsThanks.contains(itemStack)) return;
        pleaseStopDuplicatingMobsThanks.add(itemStack);
        Bukkit.getScheduler().runTaskLater(SimpleBucketMobs.getPlugin(), () -> {
            Location location = block.getLocation().toCenterLocation();
            if (!block.isPassable()) location = location.add(0, 1, 0);
            livingEntity.teleport(location);
            location.getWorld().addEntity(livingEntity);
            player.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
            pleaseStopDuplicatingMobsThanks.remove(itemStack);
        }, 2L);
    }

    public static void handleLegacyMobSpawn(Player player, ItemStack itemStack, Block block) {
        String nbt = itemStack.getPersistentDataContainer().get(InteractListeners.legacyMobTag, PersistentDataType.STRING);
        if (nbt == null || nbt.isEmpty()) return;
        if (pleaseStopDuplicatingMobsThanks.contains(itemStack)) return;
        pleaseStopDuplicatingMobsThanks.add(itemStack);
        Bukkit.getScheduler().runTaskLater(SimpleBucketMobs.getPlugin(), () -> {
            Location location = block.getLocation().toCenterLocation();
            if (!block.isPassable()) location = location.add(0, 1, 0);
            EntitySnapshot entitySnapshot = Bukkit.getEntityFactory().createEntitySnapshot(nbt);
            entitySnapshot.createEntity(location);
            player.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
            pleaseStopDuplicatingMobsThanks.remove(itemStack);
        }, 2L);
    }
}
