package adhdmc.simplebucketmobs.command.subcommand;

import adhdmc.simplebucketmobs.SimpleBucketMobs;
import adhdmc.simplebucketmobs.command.CommandHandler;
import adhdmc.simplebucketmobs.command.SubCommand;
import adhdmc.simplebucketmobs.util.Message;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reload extends SubCommand {
    public Reload() {
        super("reload", "Reloads SimpleBucketMobs", "/sbm reload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // TODO: Add simplebucketmobs.reload permission.
        SimpleBucketMobs.reloadPluginConfigs();
        sender.sendMessage(Message.COMMAND_RELOAD.getParsedMessage());
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return CommandHandler.emptyList;
    }
}
