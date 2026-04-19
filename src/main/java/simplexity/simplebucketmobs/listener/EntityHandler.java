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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public class EntityHandler {

    private static final Logger logger = SimpleBucketMobs.getPlugin().getSLF4JLogger();

    private static final List<ItemStack> pleaseStopDuplicatingMobsThanks = new ArrayList<>();

    // Cached reflection handles for NMS position setting (Paper 1.21+ Mojang mappings)
    private static Method getHandleMethod;
    private static Method setPosMethod;

    static {
        try {
            // CraftEntity.getHandle() is public and accessible via reflection
            Class<?> craftEntityClass = Class.forName("org.bukkit.craftbukkit.entity.CraftEntity");
            getHandleMethod = craftEntityClass.getMethod("getHandle");
            // net.minecraft.world.entity.Entity.setPos(double x, double y, double z)
            Class<?> nmsEntityClass = Class.forName("net.minecraft.world.entity.Entity");
            setPosMethod = nmsEntityClass.getMethod("setPos", double.class, double.class, double.class);
        } catch (Exception e) {
            logger.warn("Could not cache NMS position methods; mob release will fall back to teleport", e);
        }
    }

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
            // Set the NMS position before addEntity() so the entity spawns directly at
            // the release location rather than its stored (capture-time) position.
            // This avoids force-loading the capture chunk and prevents teleport failures
            // when that chunk is unloaded.
            setNmsPosition(livingEntity, location);
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

    /**
     * Sets the NMS entity's position directly before it is added to a world.
     * {@code Entity.teleport()} requires the entity to already be in a world, so we reach
     * into the NMS layer via reflection to update the position fields while the entity is
     * still unspawned. Paper 1.21+ uses Mojang mappings at runtime, so the method name
     * {@code setPos} is stable.
     */
    private static void setNmsPosition(Entity entity, Location location) {
        if (getHandleMethod == null || setPosMethod == null) {
            logger.warn("NMS position methods unavailable; mob may spawn at its capture location");
            return;
        }
        try {
            Object nmsEntity = getHandleMethod.invoke(entity);
            setPosMethod.invoke(nmsEntity, location.getX(), location.getY(), location.getZ());
        } catch (Exception e) {
            logger.warn("Failed to set NMS entity position before spawn", e);
        }
    }
}