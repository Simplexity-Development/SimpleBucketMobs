package simplexity.simplebucketmobs.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import simplexity.simplebucketmobs.SimpleBucketMobs;
import simplexity.simplebucketmobs.config.Config;
import simplexity.simplebucketmobs.util.Permission;

public class BucketMob implements Listener {

    public static final NamespacedKey legacyMobTag = new NamespacedKey(SimpleBucketMobs.getPlugin(), "mob_nbt");
    public static final NamespacedKey newMobTag = new NamespacedKey(SimpleBucketMobs.getPlugin(), "serialized_mob_data");

    @EventHandler
    public void onBucketMob(PlayerInteractEntityEvent interactEvent) {
        Entity entity = interactEvent.getRightClicked();
        Player player = interactEvent.getPlayer();
        if (!(entity instanceof LivingEntity livingEntity)) return;
        EntityType type = livingEntity.getType();
        if (!Config.getInstance().getAllowedBasicTypes().contains(type)) return;
        if (!(player.hasPermission(Permission.BUCKET_MOB.get() + type.toString().toLowerCase())
              || player.hasPermission(Permission.BUCKET_ALL.get()))) return;
        if (player.isSneaking()) return;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (!itemInHand.getType().equals(Material.BUCKET)) return;
        PersistentDataContainerView bucketPdcView = itemInHand.getPersistentDataContainer();
        if (bucketPdcView.has(legacyMobTag)) return;
        if (bucketPdcView.has(newMobTag)) return;
        byte[] serializedEntity = Bukkit.getUnsafe().serializeEntity(livingEntity);
        ItemStack newBucket = ItemStack.of(Material.BUCKET);
        newBucket.editPersistentDataContainer(pdc -> pdc.set(newMobTag, PersistentDataType.BYTE_ARRAY, serializedEntity));
        newBucket.setData(DataComponentTypes.CUSTOM_NAME, Component.text(type + " in a bucket"));
        player.getInventory().setItemInMainHand(newBucket);
        livingEntity.remove();
        interactEvent.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onUnbucketMob(PlayerInteractEvent interactEvent) {
        ItemStack itemStack = interactEvent.getPlayer().getInventory().getItemInMainHand();
        if (interactEvent.getAction().isLeftClick()) return;
        if (!interactEvent.hasBlock()) return;
        Block block = interactEvent.getClickedBlock();
        if (block == null || block.getType().equals(Material.AIR)) return;
        if (!itemStack.getType().equals(Material.BUCKET)) return;
        PersistentDataContainerView bucketPdc = itemStack.getPersistentDataContainer();
        if (!bucketPdc.has(newMobTag)) return;
        byte[] entityByteArray = bucketPdc.get(newMobTag, PersistentDataType.BYTE_ARRAY);
        if (entityByteArray == null) return;
        Entity deserializedEntity = Bukkit.getUnsafe().deserializeEntity(entityByteArray, block.getWorld(), true, false);
        if (!(deserializedEntity instanceof LivingEntity livingEntity)) return;
        Player player = interactEvent.getPlayer();
        if (!(player.hasPermission(Permission.BUCKET_MOB.get() + deserializedEntity.getType()) || player.hasPermission(Permission.BUCKET_ALL.get())))
            return;
        Location clickedLocation = block.getLocation();
        Location summonPoint = findSafeSummonPoint(clickedLocation.getWorld(), clickedLocation, deserializedEntity.getBoundingBox(), 5);
        if (summonPoint == null) {
            summonPoint = block.getLocation().toCenterLocation().add(0, 1, 0);
        }
        interactEvent.setCancelled(true);
        livingEntity.teleport(summonPoint);
        summonPoint.getWorld().addEntity(livingEntity);
        itemStack.editPersistentDataContainer(pdc -> pdc.remove(newMobTag));
        itemStack.setData(DataComponentTypes.CUSTOM_NAME, Component.translatable("item.minecraft.bucket").decoration(TextDecoration.ITALIC, false));
    }


    public static Location findSafeSummonPoint(World world, Location center, BoundingBox boundingBox, int radius) {
        double width = boundingBox.getWidthX();
        double depth = boundingBox.getWidthZ();
        double height = boundingBox.getHeight();

        for (int distY = -2; distY <= 2; distY++) {
            for (int distX = -radius; distX <= radius; distX++) {
                for (int distZ = -radius; distZ <= radius; distZ++) {
                    Location startingLocation = new Location(world, center.getX() + distX, distY, center.getZ() + distZ);
                    if (isAreaClear(world, startingLocation, width, height, depth)) {
                        return startingLocation.add(0.5, 0, 0.5);
                    }

                }
            }
        }
        return null;
    }

    public static boolean isAreaClear(World world, Location startLoc, double width, double height, double depth) {
        int minX = (int) Math.floor(startLoc.getX() - (width / 2));
        int maxX = (int) Math.ceil(startLoc.getX() + (width / 2));
        int minY = startLoc.getBlockY();
        int maxY = (int) Math.ceil(startLoc.getY() + height);
        int minZ = (int) Math.floor(startLoc.getZ() - (depth / 2));
        int maxZ = (int) Math.ceil(startLoc.getZ() + (depth / 2));

        Block floor = world.getBlockAt(startLoc.getBlockX(), startLoc.getBlockY() - 1, startLoc.getBlockZ());
        if (!floor.getType().isSolid()) return false;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (!block.isPassable()) return false;
                }
            }
        }
        return true;
    }

}
