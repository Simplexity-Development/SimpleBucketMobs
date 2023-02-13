package adhdmc.simplebucketmobs.command;

import adhdmc.simplebucketmobs.SimpleBucketMobs;
import adhdmc.simplebucketmobs.listener.BucketMob;
import adhdmc.simplebucketmobs.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

// TODO: Change into subcommand.
public class DebucketCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Message.ERROR_NOT_A_PLAYER.getParsedMessage());
            return true;
        }
        ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String nbt = pdc.get(BucketMob.mobNBTKey, PersistentDataType.STRING);
        if (nbt == null) {
            player.sendMessage(Message.ERROR_NO_BUCKET_MOB.getParsedMessage());
            return true;
        }
        player.sendMessage(SimpleBucketMobs.getGsonSerializer().deserialize(nbt));
        return true;
    }
}
