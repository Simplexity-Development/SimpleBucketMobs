package adhdmc.simplebucketmobs.config;

import adhdmc.simplebucketmobs.SimpleBucketMobs;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;

public class Texture {

    private static Texture instance;

    private final String fileName = "texture.yml";
    private final File dataFile = new File(SimpleBucketMobs.getPlugin().getDataFolder(), fileName);
    private final FileConfiguration texture = new YamlConfiguration();

    private Texture() {
        if (!dataFile.exists()) SimpleBucketMobs.getPlugin().saveResource(fileName, false);
        reloadTextureConfig();
    }

    public static Texture getInstance() {
        if (instance == null) instance = new Texture();
        return instance;
    }

    public FileConfiguration getTextureConfig() { return texture; }

    public void reloadTextureConfig() {
        try { texture.load(dataFile); }
        catch (IOException | InvalidConfigurationException e) { e.printStackTrace(); }
    }

    public void setCustomData(EntityType type, ItemMeta meta) {
        meta.setCustomModelData(texture.getInt(type.toString(), 0));
    }
}
