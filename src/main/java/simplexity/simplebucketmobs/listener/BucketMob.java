package simplexity.simplebucketmobs.listener;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import simplexity.simplebucketmobs.SimpleBucketMobs;
import simplexity.simplebucketmobs.config.Config;
import simplexity.simplebucketmobs.util.BucketMobPermission;

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
        if (!(player.hasPermission(BucketMobPermission.BUCKET_MOB_BASE + type.toString().toLowerCase()) || player.hasPermission(BucketMobPermission.BUCKET_ALL)))
            return;
        if (player.isSneaking()) return;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (!itemInHand.getType().equals(Material.BUCKET)) return;
        PersistentDataContainerView bucketPdcView = itemInHand.getPersistentDataContainer();
        if (bucketPdcView.has(legacyMobTag) || bucketPdcView.has(newMobTag)) return;
        ItemStack bucketItem = BucketHandler.getMobBucket(livingEntity);
        BucketHandler.addMobBucketToInventory(player, bucketItem);
        livingEntity.remove();
        interactEvent.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onUnbucketMob(PlayerInteractEvent interactEvent) {
        Player player = interactEvent.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        PersistentDataContainerView bucketPdc = itemStack.getPersistentDataContainer();
        Block block = interactEvent.getClickedBlock();
        if (interactEvent.getAction().isLeftClick()) return;
        if (!interactEvent.hasBlock()) return;
        if (block == null || block.getType().equals(Material.AIR)) return;
        if (!itemStack.getType().equals(Material.BUCKET)) return;
        if (!(bucketPdc.has(newMobTag) || bucketPdc.has(legacyMobTag))) return;
        interactEvent.setCancelled(true);
        if (bucketPdc.has(newMobTag)) EntityHandler.handleMobSpawn(player, itemStack, block);
        if (bucketPdc.has(legacyMobTag)) EntityHandler.handleLegacyMobSpawn(player, itemStack, block);
    }


}
