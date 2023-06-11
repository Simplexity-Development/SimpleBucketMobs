package simplexity.simplebucketmobs;

import simplexity.simplebucketmobs.command.CommandHandler;
import simplexity.simplebucketmobs.command.subcommand.Debucket;
import simplexity.simplebucketmobs.command.subcommand.Reload;
import simplexity.simplebucketmobs.config.Config;
import simplexity.simplebucketmobs.config.Locale;
import simplexity.simplebucketmobs.config.Texture;
import simplexity.simplebucketmobs.listener.BucketMob;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleBucketMobs extends JavaPlugin {

    private static Plugin plugin;
    private static MiniMessage miniMessage;
    private static GsonComponentSerializer gsonComponentSerializer;
    private static PlainTextComponentSerializer plainTextSerializer;

    @Override
    public void onEnable() {
        plugin = this;
        miniMessage = MiniMessage.miniMessage();
        gsonComponentSerializer = GsonComponentSerializer.gson();
        plainTextSerializer = PlainTextComponentSerializer.plainText();
        Bukkit.getPluginManager().registerEvents(new BucketMob(), this);
        registerCommands();
        reloadPluginConfigs();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Plugin getPlugin() { return plugin; }
    public static MiniMessage getMiniMessage() { return miniMessage; }
    public static GsonComponentSerializer getGsonSerializer() { return gsonComponentSerializer; }
    public static PlainTextComponentSerializer getPlainTextSerializer() { return plainTextSerializer; }

    public static void reloadPluginConfigs() {
        plugin.saveDefaultConfig();
        Config.getInstance().reloadConfig();
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
