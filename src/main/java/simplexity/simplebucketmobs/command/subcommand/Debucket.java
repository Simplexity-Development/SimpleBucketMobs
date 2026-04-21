package simplexity.simplebucketmobs.command.subcommand;

import simplexity.simplebucketmobs.command.SubCommand;
import simplexity.simplebucketmobs.listener.InteractListeners;
import simplexity.simplebucketmobs.util.Message;
import simplexity.simplebucketmobs.util.BucketMobPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Debucket extends SubCommand {
    public Debucket() {
        super("debucket", "Allows you to dump the JSON NBT data to the player chat.", "/sbm debucket", BucketMobPermission.DEBUCKET_COMMAND.getPermission());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Message.ERROR_NOT_A_PLAYER.getParsedMessage());
            return;
        }
        if (!sender.hasPermission(BucketMobPermission.DEBUCKET_COMMAND.getPermission())) {
            sender.sendMessage(Message.ERROR_COMMAND_NO_PERMISSION.getParsedMessage());
            return;
        }
        ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
        if (item == null || item.getType().isAir() || item.getItemMeta() == null) {
            player.sendMessage(Message.ERROR_NO_BUCKET_MOB.getParsedMessage());
            return;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        // Legacy format: NBT stored as a string
        String nbt = pdc.get(InteractListeners.legacyMobTag, PersistentDataType.STRING);
        if (nbt != null) {
            player.sendMessage(nbt);
            return;
        }
        // New format: entity serialized as a byte array
        byte[] serializedData = pdc.get(InteractListeners.newMobTag, PersistentDataType.BYTE_ARRAY);
        if (serializedData == null) {
            player.sendMessage(Message.ERROR_NO_BUCKET_MOB.getParsedMessage());
            return;
        }
        player.sendMessage("Serialized mob data (" + serializedData.length + " bytes, binary format)");
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}