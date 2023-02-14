package adhdmc.simplebucketmobs.listener;

import adhdmc.simplebucketmobs.SimpleBucketMobs;
import adhdmc.simplebucketmobs.config.Config;
import adhdmc.simplebucketmobs.config.Texture;
import adhdmc.simplebucketmobs.util.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftLivingEntity;
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

    public static final NamespacedKey mobNBTKey = new NamespacedKey(SimpleBucketMobs.getPlugin(), "mob_nbt");

    // TODO: Handle normal bucket use cases such as Cow Milking
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void bucketMob(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof LivingEntity entity)) return;
        // TODO: Don't forget to uncomment this: if (entity.getType() == EntityType.PLAYER) return;
        if (!Config.getInstance().getAllowedTypes().contains(entity.getType())) return;
        // TODO: Permission Check.
        // TODO: Check disallowed attributes.
        ItemStack bucket = event.getPlayer().getEquipment().getItemInMainHand();
        if (bucket.getType() != Material.BUCKET) return;
        if (bucket.getItemMeta().getPersistentDataContainer().has(mobNBTKey)) {
            event.setCancelled(true);
            return;
        }

        ItemStack mobBucket = new ItemStack(Material.BUCKET);
        String serializedNbt = serializeNBT(entity);

        ItemMeta meta = mobBucket.getItemMeta();
        PersistentDataContainer bucketPDC = meta.getPersistentDataContainer();
        bucketPDC.set(mobNBTKey, PersistentDataType.STRING, serializedNbt);

        // TODO: Make Configurable
        meta.displayName(MiniMessage.miniMessage().deserialize("<aqua>Mob Bucket"));
        Texture.getInstance().setCustomData(entity.getType(), meta);
        mobBucket.setItemMeta(meta);
        bucket.subtract();
        event.getPlayer().getInventory().addItem(mobBucket);
        entity.remove();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void unbucketMob(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Location interactLoc = event.getInteractionPoint();
        if (interactLoc == null) return;
        ItemStack bucket = event.getPlayer().getEquipment().getItemInMainHand();
        if (bucket.getType() != Material.BUCKET) return;
        if (!bucket.getItemMeta().getPersistentDataContainer().has(mobNBTKey)) return;

        String serializedNbt = bucket.getItemMeta().getPersistentDataContainer().get(mobNBTKey, PersistentDataType.STRING);

        try { if (serializedNbt != null) applyNBT(interactLoc, serializedNbt); }
        catch (IOException | CommandSyntaxException e) {
            event.getPlayer().sendMessage(Message.ERROR_FAILED_DESERIALIZATION.getParsedMessage());
            e.printStackTrace();
            return;
        }
        catch (IllegalArgumentException e) {
            event.getPlayer().sendMessage(Message.ERROR_NO_BUCKET_MOB.getParsedMessage());
            e.printStackTrace();
            return;
        }

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            bucket.subtract();
            event.getPlayer().getInventory().addItem(new ItemStack(Material.BUCKET));
        }
    }

    /**
     * Serializes the NBT Data from the LivingEntity.
     * @param e LivingEntity
     * @return String serialization of the LivingEntity.
     */
    private String serializeNBT(LivingEntity e) {
        CompoundTag tag = new CompoundTag();
        ((CraftLivingEntity) e).getHandle().save(tag);
        return tag.getAsString();
    }

    /**
     * Deserializes the NBT Data into the LivingEntity.
     * @param location Location to spawn Mob.
     * @param serializedNbt NBT as a String
     * @exception IllegalArgumentException Invalid Mob Type Found
     * @exception IOException Failed to read NBT Tags.
     * @exception CommandSyntaxException What.
     */
    private void applyNBT(Location location, String serializedNbt) throws IllegalArgumentException, IOException, CommandSyntaxException {
        CompoundTag tag = TagParser.parseTag(serializedNbt);
        Tag idTag = tag.get("id");
        // TODO: Maybe throw exception.
        if (idTag == null) return;
        String id = idTag.getAsString().split(":")[1].toUpperCase();
        EntityType mobType = EntityType.valueOf(id);
        Entity entity = location.getWorld().spawnEntity(location, mobType, CreatureSpawnEvent.SpawnReason.CUSTOM);
        CompoundTag newLoc = new CompoundTag();
        ((CraftLivingEntity) entity).getHandle().save(newLoc);
        tag.put("Motion", newLoc.get("Motion"));
        tag.put("Pos", newLoc.get("Pos"));
        tag.put("Rotation", newLoc.get("Rotation"));
        tag.put("UUID", newLoc.get("UUID"));
        ((CraftLivingEntity) entity).getHandle().load(tag);
    }

}
