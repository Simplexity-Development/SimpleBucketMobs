package simplexity.simplebucketmobs;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simplebucketmobs.command.CommandHandler;
import simplexity.simplebucketmobs.command.subcommand.Debucket;
import simplexity.simplebucketmobs.command.subcommand.Reload;
import simplexity.simplebucketmobs.config.ConfigHandler;
import simplexity.simplebucketmobs.config.Locale;
import simplexity.simplebucketmobs.config.Texture;
import simplexity.simplebucketmobs.listener.InteractListeners;
import simplexity.simplebucketmobs.util.BucketMobPermission;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class SimpleBucketMobs extends JavaPlugin {

    private static Plugin plugin;
    private static MiniMessage miniMessage;

    @Override
    public void onEnable() {
        plugin = this;
        miniMessage = MiniMessage.miniMessage();
        registerPermissions();
        Bukkit.getPluginManager().registerEvents(new InteractListeners(), this);
        registerCommands();
        reloadPluginConfigs();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public static void reloadPluginConfigs() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
        ConfigHandler.getInstance().reloadConfig();
        Locale.getInstance().reloadLocale();
        Texture.getInstance().reloadTextureConfig();
    }

    private void registerPermissions() {
        for (BucketMobPermission perm : BucketMobPermission.values()) {
            Bukkit.getPluginManager().addPermission(perm.getPermission());
        }
    }

    private void registerCommands() {
        CommandHandler.subcommandList.clear();
        CommandHandler.subcommandList.put("reload", new Reload());
        CommandHandler.subcommandList.put("debucket", new Debucket());
        CommandHandler handler = new CommandHandler();

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(
                "simplebucketmobs",
                "SimpleBucketMobs Base Command.",
                List.of("sbm", "simplebucketmob"),
                handler
            );
        });
    }
}
