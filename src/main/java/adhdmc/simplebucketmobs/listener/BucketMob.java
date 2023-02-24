package adhdmc.simplebucketmobs.listener;

import adhdmc.simplebucketmobs.SimpleBucketMobs;
import adhdmc.simplebucketmobs.config.Config;
import adhdmc.simplebucketmobs.config.Texture;
import adhdmc.simplebucketmobs.util.Message;
import adhdmc.simplebucketmobs.util.Permission;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;

import java.io.IOException;

public class BucketMob implements Listener {

    public static final NamespacedKey mobNBTKey = new NamespacedKey(SimpleBucketMobs.getPlugin(), "mob_nbt");

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void bucketMob(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        if (!(event.getRightClicked() instanceof LivingEntity entity)) return;
        if (entity.getType() == EntityType.PLAYER) return;
        if (Config.getInstance().isNoHostileTargeting()
                && entity instanceof Monster monster
                && monster.getTarget() != null
                && monster.getTarget().equals(player)) {
            player.sendMessage(Message.ERROR_BUCKET_HOSTILE_TARGETING.getParsedMessage());
            return;
        }
        if (!Config.getInstance().getAllowedTypes().contains(entity.getType())) return;
        if (!(player.hasPermission(Permission.BUCKET_ALL.get()) || player.hasPermission(Permission.BUCKET_MOB.get() + entity.getType()))) {
            player.sendMessage(Message.ERROR_BUCKET_NO_PERMISSION.getParsedMessage());
            return;
        }
        // TODO: Health Threshold Requirement / Health Check Bypass Permission (Per Mob)
        // TODO: Check disallowed attributes.
        ItemStack bucket = player.getEquipment().getItemInMainHand();
        // TODO: Allow for different bucket types.
        if (bucket.getType() != Material.BUCKET) return;
        if (bucket.getItemMeta().getPersistentDataContainer().has(mobNBTKey)) {
            event.setCancelled(true);
            return;
        }

        ItemStack mobBucket = new ItemStack(Material.BUCKET);
        Entity vehicle = entity.getVehicle();
        if (vehicle != null) vehicle.removePassenger(entity);
        CompoundTag tag = serializeNBT(entity);
        String serializedNbt = tag.getAsString();

        ItemMeta meta = mobBucket.getItemMeta();
        PersistentDataContainer bucketPDC = meta.getPersistentDataContainer();
        bucketPDC.set(mobNBTKey, PersistentDataType.STRING, serializedNbt);

        String entityType = entity.getType().toString();
        String entityTypeName = nameCase(entityType);
        Component customName = entity.customName();
        Component entityTypeComponent = SimpleBucketMobs.getPlainTextSerializer().deserialize(entityTypeName);
        meta.displayName(SimpleBucketMobs.getMiniMessage().deserialize(
                "<!i>" + Config.getInstance().getBucketTitle(),
                Placeholder.parsed("type", entityType),
                Placeholder.parsed("type_name_cased", entityTypeName),
                Placeholder.component("display_name", customName != null ? customName : entityTypeComponent)
        ));
        Texture.getInstance().setCustomData(entity.getType(), meta, tag);
        mobBucket.setItemMeta(meta);
        if (player.getGameMode() != GameMode.CREATIVE) bucket.subtract();
        player.getInventory().addItem(mobBucket);
        try {
            // TODO: Configurable?
            String soundName = "ENTITY_" + entity.getType() + "_HURT";
            player.playSound(player.getLocation(), Sound.valueOf(soundName), 0.75f, 1.0f);
        } catch (IllegalArgumentException ignored) { }
        entity.remove();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void unbucketMob(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Location interactLoc = event.getInteractionPoint();
        if (interactLoc == null) return;

        Player player = event.getPlayer();
        ItemStack bucket = player.getEquipment().getItemInMainHand();
        if (bucket.getType() != Material.BUCKET) return;
        if (!bucket.getItemMeta().getPersistentDataContainer().has(mobNBTKey)) return;

        String serializedNbt = bucket.getItemMeta().getPersistentDataContainer().get(mobNBTKey, PersistentDataType.STRING);

        try { if (serializedNbt != null) applyNBT(interactLoc, serializedNbt, event.getBlockFace()); }
        catch (IOException | CommandSyntaxException e) {
            player.sendMessage(Message.ERROR_FAILED_DESERIALIZATION.getParsedMessage());
            e.printStackTrace();
            return;
        }
        catch (IllegalArgumentException e) {
            player.sendMessage(Message.ERROR_NO_BUCKET_MOB.getParsedMessage());
            e.printStackTrace();
            return;
        }

        // TODO: Make configurable.
        player.playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL_POWDER_SNOW, 1.0f, 1.0f);

        if (player.getGameMode() != GameMode.CREATIVE) {
            bucket.subtract();
            player.getInventory().addItem(new ItemStack(Material.BUCKET));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void noBucketLiquid(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        ItemStack bucket = player.getEquipment().getItem(event.getHand());
        if (bucket.getItemMeta().getPersistentDataContainer().has(mobNBTKey)) {
            event.setCancelled(true);
        }
        // TODO: Make it so that when performing bucketMob and unbucketMob, the bucket does not collect liquid.
    }

    /**
     * Serializes the NBT Data from the LivingEntity.
     * @param e LivingEntity
     * @return String serialization of the LivingEntity.
     */
    private CompoundTag serializeNBT(LivingEntity e) {
        CompoundTag tag = new CompoundTag();
        ((CraftLivingEntity) e).getHandle().save(tag);
        return tag;
    }

    /**
     * Deserializes the NBT Data into the LivingEntity.
     * @param location Location to spawn Mob.
     * @param serializedNbt NBT as a String
     * @exception IllegalArgumentException Invalid Mob Type Found
     * @exception IOException Failed to read NBT Tags.
     * @exception CommandSyntaxException What.
     */
    private void applyNBT(Location location, String serializedNbt, BlockFace face) throws IllegalArgumentException, IOException, CommandSyntaxException {
        CompoundTag tag = TagParser.parseTag(serializedNbt);
        Tag idTag = tag.get("id");
        // TODO: Maybe throw exception.
        if (idTag == null) return;
        String id = idTag.getAsString().split(":")[1].toUpperCase();
        EntityType mobType = EntityType.valueOf(id);
        Entity entity = location.getWorld().spawnEntity(location, mobType, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.teleport(adjustLoc(location, face, entity.getBoundingBox()));
        CompoundTag newLoc = new CompoundTag();
        ((CraftLivingEntity) entity).getHandle().save(newLoc);
        tag.put("Motion", newLoc.get("Motion"));
        tag.put("Pos", newLoc.get("Pos"));
        tag.put("Rotation", newLoc.get("Rotation"));
        tag.put("UUID", newLoc.get("UUID"));
        ((CraftLivingEntity) entity).getHandle().load(tag);
    }

    /**
     * Converts a string to Name Case
     * @param input String
     * @return String but using Name Case
     * @implNote Thanks Baeldung (https://www.baeldung.com/java-string-title-case)
     */
    private String nameCase(String input) {
        if (input == null || input.isBlank()) return input;
        StringBuilder nameCased = new StringBuilder();
        boolean toUpper = true;
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) toUpper = true;
            else if (toUpper) {
                c = Character.toTitleCase(c);
                toUpper = false;
            }
            else {
                c = Character.toLowerCase(c);
            }
            nameCased.append(c);
        }
        return nameCased.toString();
    }

    /**
     * Adjusts the location given
     * @param interactionPoint Location of Interaction Point
     * @param face Block Face of Interaction
     * @return The same Location.
     */
    private Location adjustLoc(Location interactionPoint, BlockFace face, BoundingBox entityBox) {
        double height = entityBox.getHeight();
        double widthX = entityBox.getWidthX();
        double widthZ = entityBox.getWidthZ();
        switch (face) {
            case DOWN -> interactionPoint.add(0, -1*height, 0);
            case EAST -> interactionPoint.add(.5*widthX, 0, 0);
            case WEST -> interactionPoint.add(-.5*widthX, 0, 0);
            case NORTH -> interactionPoint.add(0, 0, -.5*widthZ);
            case SOUTH -> interactionPoint.add(0, 0, .5*widthZ);
        }
        return interactionPoint;
    }

}
