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

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public class EntityHandler {

    private static final Logger logger = SimpleBucketMobs.getPlugin().getSLF4JLogger();

    public static void handleMobSpawn(Player player, ItemStack itemStack, Block block){
        byte[] entityBytes = itemStack.getPersistentDataContainer().get(BucketMob.newMobTag, PersistentDataType.BYTE_ARRAY);
        if (entityBytes == null) return;
        Entity deserializedEntity = Bukkit.getUnsafe().deserializeEntity(entityBytes, block.getWorld(), false, false);
        if (!(deserializedEntity instanceof LivingEntity livingEntity)) {
            logger.warn("Entity saved in this bucket was not a living entity, idk how you managed to do that but it's not gonna spawn. Entity info: {}", deserializedEntity);
            return;
        }
        Location location = block.getLocation().toCenterLocation();
        if (!block.isPassable()) location = location.add(0, 1, 0);
        livingEntity.teleport(location);
        location.getWorld().addEntity(livingEntity);
        player.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
    }

    public static void handleLegacyMobSpawn(Player player, ItemStack itemStack, Block block){
        String nbt = itemStack.getPersistentDataContainer().get(BucketMob.legacyMobTag, PersistentDataType.STRING);
        if (nbt == null || nbt.isEmpty()) return;
        Location location = block.getLocation().toCenterLocation();
        if (!block.isPassable()) location = location.add(0, 1, 0);
        EntitySnapshot entitySnapshot = Bukkit.getEntityFactory().createEntitySnapshot(nbt);
        entitySnapshot.createEntity(location);
        player.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
    }
}
