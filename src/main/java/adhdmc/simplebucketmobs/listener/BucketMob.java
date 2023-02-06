package adhdmc.simplebucketmobs.listener;

import adhdmc.simplebucketmobs.SimpleBucketMobs;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;

public class BucketMob implements Listener {

    NamespacedKey mobNBTKey = new NamespacedKey(SimpleBucketMobs.getPlugin(), "mob_nbt");
    NamespacedKey mobTypeKey = new NamespacedKey(SimpleBucketMobs.getPlugin(), "mob_type");

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void bucketMob(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof LivingEntity)) return;
        ItemStack bucket = event.getPlayer().getEquipment().getItemInMainHand();
        if (bucket.getType() != Material.BUCKET) return;
        if (bucket.getItemMeta().getPersistentDataContainer().has(mobTypeKey)) return;
        ItemStack mobBucket = new ItemStack(Material.BUCKET);
        byte[] serializedMob;
        try {
            serializedMob = event.getRightClicked().getPersistentDataContainer().serializeToBytes();
        } catch (IOException e) {
            event.getPlayer().sendRichMessage("<red>Failed to bucket mob (Serialization).");
            return;
        }
        ItemMeta meta = mobBucket.getItemMeta();
        PersistentDataContainer bucketPDC = meta.getPersistentDataContainer();
        bucketPDC.set(mobNBTKey, PersistentDataType.BYTE_ARRAY, serializedMob);
        bucketPDC.set(mobTypeKey, PersistentDataType.STRING, event.getRightClicked().getType().toString());
        meta.displayName(MiniMessage.miniMessage().deserialize("<aqua>Mob Bucket"));
        mobBucket.setItemMeta(meta);
        bucket.subtract();
        event.getPlayer().getInventory().addItem(mobBucket);
        event.getRightClicked().remove();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void unbucketMob(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Location interactLoc = event.getInteractionPoint();
        if (interactLoc == null) return;
        ItemStack bucket = event.getPlayer().getEquipment().getItemInMainHand();
        if (bucket.getType() != Material.BUCKET) return;
        if (!bucket.getItemMeta().getPersistentDataContainer().has(mobNBTKey)) return;
        String mobTypeString = bucket.getItemMeta().getPersistentDataContainer().get(mobTypeKey, PersistentDataType.STRING);
        byte[] serializedMob = bucket.getItemMeta().getPersistentDataContainer().get(mobNBTKey, PersistentDataType.BYTE_ARRAY);
        EntityType mobType;
        try { mobType = EntityType.valueOf(mobTypeString); }
        catch (IllegalArgumentException e) {
            event.getPlayer().sendRichMessage("<red>Failed to unbucket mob (IllegalArgumentException).");
            return;
        }
        Entity entity = interactLoc.getWorld().spawnEntity(interactLoc, mobType, CreatureSpawnEvent.SpawnReason.CUSTOM);
        try { entity.getPersistentDataContainer().readFromBytes(serializedMob); }
        catch (IOException e) {
            event.getPlayer().sendRichMessage("<red>Failed to unbucket mob (Deserialization).");
            return;
        }
        bucket.subtract();
        event.getPlayer().getInventory().addItem(new ItemStack(Material.BUCKET));
    }

}
