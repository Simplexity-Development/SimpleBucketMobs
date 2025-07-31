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
        super("debucket", "Allows you to dump the JSON NBT data to the player chat.", "/sbm debucket", BucketMobPermission.DEBUCKET_COMMAND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Message.ERROR_NOT_A_PLAYER.getParsedMessage());
            return;
        }
        if (!sender.hasPermission(BucketMobPermission.DEBUCKET_COMMAND)) {
            sender.sendMessage(Message.ERROR_COMMAND_NO_PERMISSION.getParsedMessage());
            return;
        }
        ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String nbt = pdc.get(InteractListeners.legacyMobTag, PersistentDataType.STRING);
        if (nbt == null) {
            player.sendMessage(Message.ERROR_NO_BUCKET_MOB.getParsedMessage());
            return;
        }
        player.sendMessage(nbt);
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
