package adhdmc.simplebucketmobs.command.subcommand;

import adhdmc.simplebucketmobs.command.SubCommand;
import adhdmc.simplebucketmobs.listener.BucketMob;
import adhdmc.simplebucketmobs.util.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Debucket extends SubCommand {
    public Debucket() {
        super("debucket", "Allows you to dump the JSON NBT data to the player chat.", "/sbm debucket");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Message.ERROR_NOT_A_PLAYER.getParsedMessage());
            return;
        }
        ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String nbt = pdc.get(BucketMob.mobNBTKey, PersistentDataType.STRING);
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
