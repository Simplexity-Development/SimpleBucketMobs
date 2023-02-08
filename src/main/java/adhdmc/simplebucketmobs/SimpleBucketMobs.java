package adhdmc.simplebucketmobs;

import adhdmc.simplebucketmobs.command.DebucketCommand;
import adhdmc.simplebucketmobs.listener.BucketMob;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleBucketMobs extends JavaPlugin {

    private static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(new BucketMob(), this);
        this.getCommand("debucket").setExecutor(new DebucketCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Plugin getPlugin() { return plugin; }
}
