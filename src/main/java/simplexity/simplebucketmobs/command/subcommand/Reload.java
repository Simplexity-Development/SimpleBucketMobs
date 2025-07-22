package simplexity.simplebucketmobs.command.subcommand;

import simplexity.simplebucketmobs.SimpleBucketMobs;
import simplexity.simplebucketmobs.command.CommandHandler;
import simplexity.simplebucketmobs.command.SubCommand;
import simplexity.simplebucketmobs.util.Message;
import simplexity.simplebucketmobs.util.BucketMobPermission;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reload extends SubCommand {
    public Reload() {
        super("reload", "Reloads SimpleBucketMobs", "/sbm reload", BucketMobPermission.COMMAND_RELOAD);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(BucketMobPermission.COMMAND_RELOAD)) {
            sender.sendMessage(Message.ERROR_COMMAND_NO_PERMISSION.getParsedMessage());
            return;
        }
        SimpleBucketMobs.reloadPluginConfigs();
        sender.sendMessage(Message.COMMAND_RELOAD.getParsedMessage());
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return CommandHandler.emptyList;
    }
}
