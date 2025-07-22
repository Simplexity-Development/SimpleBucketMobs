package simplexity.simplebucketmobs.listener;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityFactory;
import org.bukkit.entity.EntitySnapshot;
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
import org.checkerframework.checker.units.qual.C;
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
        ItemStack bucketItem = BucketHandler.getMobBucket(livingEntity);
        BucketHandler.addBucketToInventory(player, bucketItem);
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
        if (bucketPdc.has(legacyMobTag)) {
            String entityNbt = bucketPdc.get(legacyMobTag, PersistentDataType.STRING);
            spawnOldMobs(entityNbt, block.getLocation().toCenterLocation().add(0, 1, 0));
            interactEvent.setCancelled(true);
            interactEvent.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
            return;
        }
        if (!bucketPdc.has(newMobTag)) return;
        byte[] entityByteArray = bucketPdc.get(newMobTag, PersistentDataType.BYTE_ARRAY);
        if (entityByteArray == null) return;
        Entity deserializedEntity = Bukkit.getUnsafe().deserializeEntity(entityByteArray, block.getWorld(), true, false);
        if (!(deserializedEntity instanceof LivingEntity livingEntity)) return;
        Player player = interactEvent.getPlayer();
        if (!(player.hasPermission(Permission.BUCKET_MOB.get() + deserializedEntity.getType()) || player.hasPermission(Permission.BUCKET_ALL.get())))
            return;
        Location clickedLocation = block.getLocation();
        Location summonPoint = block.getLocation().toCenterLocation().add(0, 1, 0);

        if (Bukkit.getEntity(livingEntity.getUniqueId()) != null) livingEntity = (LivingEntity) livingEntity.copy();
        interactEvent.setCancelled(true);
        livingEntity.teleport(summonPoint);
        summonPoint.getWorld().addEntity(livingEntity);
        player.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
    }


    private void spawnOldMobs(String nbt, Location location) {
        EntitySnapshot entitySnapshot = Bukkit.getEntityFactory().createEntitySnapshot(nbt);
        entitySnapshot.createEntity(location);
    }


}
