package adhdmc.simplebucketmobs.listener;

import adhdmc.simplebucketmobs.SimpleBucketMobs;
import adhdmc.simplebucketmobs.config.Config;
import adhdmc.simplebucketmobs.util.Message;
import com.google.common.base.Charsets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.BucketItem;
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

import java.io.*;

public class BucketMob implements Listener {

    public static final NamespacedKey mobNBTKey = new NamespacedKey(SimpleBucketMobs.getPlugin(), "mob_nbt");
    public static final NamespacedKey mobTypeKey = new NamespacedKey(SimpleBucketMobs.getPlugin(), "mob_type");

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void bucketMob(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof LivingEntity entity)) return;
        if (!Config.getInstance().getAllowedTypes().contains(entity.getType())) return;
        // TODO: Permission Check.
        // TODO: Check disallowed attributes.
        ItemStack bucket = event.getPlayer().getEquipment().getItemInMainHand();
        if (bucket.getType() != Material.BUCKET) return;
        if (bucket.getItemMeta().getPersistentDataContainer().has(mobTypeKey)) return;

        ItemStack mobBucket = new ItemStack(Material.BUCKET);
        String serializedNbt;
        serializedNbt = serializeNBT(entity);

        ItemMeta meta = mobBucket.getItemMeta();
        PersistentDataContainer bucketPDC = meta.getPersistentDataContainer();
        bucketPDC.set(mobNBTKey, PersistentDataType.STRING, serializedNbt);
        bucketPDC.set(mobTypeKey, PersistentDataType.STRING, entity.getType().toString());

        // TODO: Make Configurable
        meta.displayName(MiniMessage.miniMessage().deserialize("<aqua>Mob Bucket"));
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

        String mobTypeString = bucket.getItemMeta().getPersistentDataContainer().get(mobTypeKey, PersistentDataType.STRING);
        String serializedNbt = bucket.getItemMeta().getPersistentDataContainer().get(mobNBTKey, PersistentDataType.STRING);

        EntityType mobType;
        try { mobType = EntityType.valueOf(mobTypeString); }
        catch (IllegalArgumentException e) {
            event.getPlayer().sendMessage(Message.ERROR_NO_BUCKET_MOB.getParsedMessage());
            e.printStackTrace();
            return;
        }

        LivingEntity entity = (LivingEntity) interactLoc.getWorld().spawnEntity(interactLoc, mobType, CreatureSpawnEvent.SpawnReason.CUSTOM);
        try { if (serializedNbt != null) applyNBT(entity, serializedNbt); }
        catch (IOException | CommandSyntaxException e) {
            event.getPlayer().sendMessage(Message.ERROR_FAILED_DESERIALIZATION.getParsedMessage());
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
     * @param e LivingEntity
     * @exception IOException Failed to read NBT Tags.
     * @exception CommandSyntaxException What.
     */
    private void applyNBT(LivingEntity e, String serializedNbt) throws IOException, CommandSyntaxException {
        CompoundTag tag = TagParser.parseTag(serializedNbt);
        CompoundTag newLoc = new CompoundTag();
        ((CraftLivingEntity) e).getHandle().save(newLoc);
        tag.put("Motion", newLoc.get("Motion"));
        tag.put("Pos", newLoc.get("Pos"));
        tag.put("Rotation", newLoc.get("Rotation"));
        tag.put("UUID", newLoc.get("UUID"));
        ((CraftLivingEntity) e).getHandle().load(tag);
    }

}
