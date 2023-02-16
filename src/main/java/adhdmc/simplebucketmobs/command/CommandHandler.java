package adhdmc.simplebucketmobs.command;

import adhdmc.simplebucketmobs.util.Message;
import adhdmc.simplebucketmobs.util.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommandHandler implements TabExecutor {

    public static final List<String> emptyList = Collections.unmodifiableList(new ArrayList<>());
    public static HashMap<String, SubCommand> subcommandList = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            // TODO: Help
            return true;
        }
        String subcommand = args[0].toLowerCase();
        if (subcommandList.containsKey(subcommand)) {
            subcommandList.get(subcommand).execute(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            sender.sendMessage(Message.ERROR_COMMAND_NOT_FOUND.getParsedMessage());
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {        if (args.length == 0) return new ArrayList<>();
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            for (SubCommand cmd : subcommandList.values()) {
                if (sender.hasPermission(cmd.getPermission()) && cmd.getName().contains(args[0])) list.add(cmd.getName());
            }
            return list;
        }
        String subcommand = args[0].toLowerCase();
        if (subcommandList.containsKey(subcommand) && sender.hasPermission(subcommandList.get(subcommand).getPermission())) {
            return subcommandList.get(subcommand).getSubcommandArguments(sender, args);
        }
        return new ArrayList<>();
    }
}
