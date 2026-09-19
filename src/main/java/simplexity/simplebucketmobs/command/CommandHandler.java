package simplexity.simplebucketmobs.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import simplexity.simplebucketmobs.util.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CommandHandler implements BasicCommand {

    public static final List<String> emptyList = Collections.unmodifiableList(new ArrayList<>());
    public static HashMap<String, SubCommand> subcommandList = new HashMap<>();

    @Override
    public void execute(@NotNull CommandSourceStack source, @NotNull String[] args) {
        CommandSender sender = source.getSender();
        if (args.length == 0) {
            // TODO: Help
            return;
        }
        String subcommand = args[0].toLowerCase();
        if (subcommandList.containsKey(subcommand)) {
            subcommandList.get(subcommand).execute(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            sender.sendMessage(Message.ERROR_COMMAND_NOT_FOUND.getParsedMessage());
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack source, @NotNull String[] args) {
        CommandSender sender = source.getSender();
        if (args.length == 0) return new ArrayList<>();
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            for (SubCommand cmd : subcommandList.values()) {
                if (sender.hasPermission(cmd.getPermission()) && cmd.getName().contains(args[0])) list.add(cmd.getName());
            }
            return list;
        }
        String subcommand = args[0].toLowerCase();
        if (subcommandList.containsKey(subcommand) && sender.hasPermission(subcommandList.get(subcommand).getPermission())) {
            List<String> suggestions = subcommandList.get(subcommand).getSubcommandArguments(sender, args);
            return suggestions != null ? suggestions : List.of();
        }
        return new ArrayList<>();
    }
}