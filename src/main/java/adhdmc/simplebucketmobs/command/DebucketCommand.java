package adhdmc.simplebucketmobs.command;

import adhdmc.simplebucketmobs.listener.BucketMob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class DebucketCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(BucketMob.mobNBTKey)) return true;
        String nbt = pdc.get(BucketMob.mobNBTKey, PersistentDataType.STRING);
        if (nbt == null) return true;
        player.sendMessage(nbt);
        return true;
    }
}
