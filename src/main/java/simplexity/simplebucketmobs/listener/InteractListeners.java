package simplexity.simplebucketmobs.listener;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import simplexity.simplebucketmobs.SimpleBucketMobs;
import simplexity.simplebucketmobs.config.ConfigHandler;
import simplexity.simplebucketmobs.util.BucketMobPermission;
import simplexity.simplebucketmobs.util.Message;

public class InteractListeners implements Listener {

    public static final NamespacedKey legacyMobTag = new NamespacedKey(SimpleBucketMobs.getPlugin(), "mob_nbt");
    public static final NamespacedKey newMobTag = new NamespacedKey(SimpleBucketMobs.getPlugin(), "serialized_mob_data");

    @EventHandler(ignoreCancelled = true)
    public void onBucketMob(PlayerInteractEntityEvent interactEvent) {
        // Only process main-hand interactions so we don't act on off-hand triggers
        if (!interactEvent.getHand().equals(EquipmentSlot.HAND)) return;
        Entity entity = interactEvent.getRightClicked();
        Player player = interactEvent.getPlayer();
        if (!(entity instanceof LivingEntity livingEntity)) return;
        EntityType type = livingEntity.getType();
        if (!ConfigHandler.getInstance().bucketingAllowed(type)) return;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (!itemInHand.getType().equals(Material.BUCKET)) return;
        PersistentDataContainerView bucketPdcView = itemInHand.getPersistentDataContainer();
        if (bucketPdcView.has(legacyMobTag) || bucketPdcView.has(newMobTag)) return;
        // Permission failure falls back to vanilla — player can still interact normally (e.g. milk a cow)
        if (ConfigHandler.getInstance().requiresExplicitPermission(type) &&
            !player.hasPermission(BucketMobPermission.getBucketMobPermission(type))) {
            player.sendRichMessage(Message.ERROR_BUCKET_NO_PERMISSION.getMessage(),
                    Placeholder.parsed("prefix", Message.PREFIX.getMessage()));
            return;
        }
        // From here the plugin owns the interaction; cancel so vanilla can't fire (e.g. cow milking sound)
        interactEvent.setCancelled(true);
        if (ConfigHandler.getInstance().isSneakRequired(type) && !player.isSneaking()) return;
        if (livingEntity instanceof Monster monster) {
            LivingEntity target = monster.getTarget();
            // isAggressive() is unreliable for mobs like Slime/Spider — targeting the player is sufficient
            if (target != null && !ConfigHandler.getInstance().canPickupWhenAggro(type) && target.equals(player)) {
                player.sendRichMessage(Message.ERROR_BUCKET_HOSTILE_TARGETING.getMessage(),
                        Placeholder.parsed("prefix", Message.PREFIX.getMessage()));
                return;
            }
        }
        ItemStack bucketItem = BucketHandler.getMobBucket(livingEntity);
        BucketHandler.addMobBucketToInventory(player, bucketItem);
        player.playSound(Sound.sound(Key.key("minecraft", "item.bucket.fill_fish"), Sound.Source.PLAYER, 1.0f, 1.0f));
        livingEntity.remove();
    }

    @EventHandler(ignoreCancelled = true)
    public void onUnbucketMob(PlayerInteractEvent interactEvent) {
        // Only process main-hand interactions; off-hand fires a duplicate event
        EquipmentSlot hand = interactEvent.getHand();
        if (hand == null || !hand.equals(EquipmentSlot.HAND)) return;
        Player player = interactEvent.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        PersistentDataContainerView bucketPdc = itemStack.getPersistentDataContainer();
        Block block = interactEvent.getClickedBlock();
        if (interactEvent.getAction().isLeftClick()) return;
        if (!interactEvent.hasBlock()) return;
        if (block == null || block.getType().equals(Material.AIR)) return;
        if (!(itemStack.getType().equals(Material.BUCKET) || itemStack.getType().equals(ConfigHandler.getInstance().getMobBucketMaterial()))) return;
        if (!(bucketPdc.has(newMobTag) || bucketPdc.has(legacyMobTag))) return;
        interactEvent.setCancelled(true);
        if (bucketPdc.has(newMobTag)) EntityHandler.handleMobSpawn(player, itemStack, block);
        if (bucketPdc.has(legacyMobTag)) EntityHandler.handleLegacyMobSpawn(player, itemStack, block);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBucketEvent(PlayerBucketFillEvent bucketFillEvent){
        EquipmentSlot handUsed = bucketFillEvent.getHand();
        ItemStack itemUsed;
        if (handUsed.equals(EquipmentSlot.HAND)) {
            itemUsed = bucketFillEvent.getPlayer().getInventory().getItemInMainHand();
        } else {
            itemUsed = bucketFillEvent.getPlayer().getInventory().getItemInOffHand();
        }
        if (itemUsed.getPersistentDataContainer().has(legacyMobTag) ||
            itemUsed.getPersistentDataContainer().has(newMobTag)) bucketFillEvent.setCancelled(true);
    }
}
