package simplexity.simplebucketmobs;

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

public final class SimpleBucketMobs extends JavaPlugin {

    private static Plugin plugin;
    private static MiniMessage miniMessage;

    @Override
    public void onEnable() {
        plugin = this;
        miniMessage = MiniMessage.miniMessage();
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
        ConfigHandler.getInstance().reloadConfig();
        Locale.getInstance().reloadLocale();
        Texture.getInstance().reloadTextureConfig();
    }

    private void registerCommands() {
        this.getCommand("simplebucketmobs").setExecutor(new CommandHandler());
        CommandHandler.subcommandList.clear();
        CommandHandler.subcommandList.put("reload", new Reload());
        CommandHandler.subcommandList.put("debucket", new Debucket());
    }
}
