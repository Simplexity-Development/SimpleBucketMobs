package adhdmc.simplebucketmobs;

import adhdmc.simplebucketmobs.command.DebucketCommand;
import adhdmc.simplebucketmobs.listener.BucketMob;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class SimpleBucketMobs extends JavaPlugin {

    private static Plugin plugin;
    private static MiniMessage miniMessage;
    private static GsonComponentSerializer gsonComponentSerializer;

    @Override
    public void onEnable() {
        plugin = this;
        miniMessage = MiniMessage.miniMessage();
        gsonComponentSerializer = GsonComponentSerializer.gson();
        Bukkit.getPluginManager().registerEvents(new BucketMob(), this);
        this.getCommand("debucket").setExecutor(new DebucketCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Plugin getPlugin() { return plugin; }
    public static MiniMessage getMiniMessage() { return miniMessage; }
    public static GsonComponentSerializer getGsonSerializer() { return gsonComponentSerializer; }
}
