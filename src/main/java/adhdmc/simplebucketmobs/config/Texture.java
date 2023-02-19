package adhdmc.simplebucketmobs.config;

import adhdmc.simplebucketmobs.SimpleBucketMobs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.configuration.ConfigurationSection;
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

    public void setCustomData(EntityType type, ItemMeta meta, CompoundTag tag) {
        ConfigurationSection section = texture.getConfigurationSection(type.toString());
        if (section == null) return;
        meta.setCustomModelData(section.getInt("default", 0));
        if (surprise(type, meta, tag)) return;
        String value = null;
        // Until we hit a dead end or found the path...
        while (true) {
            String ymlKey = null;
            // Find a valid key to look into.
            for (String key : section.getKeys(false)) {
                // If the tag does not exist, ignore this value.
                if (!tag.contains(key)) continue;
                ymlKey = key;
                break;
            }
            if (ymlKey == null) break;
            // If the key does not lead to a configuration section, we cannot continue.
            if (!section.isConfigurationSection(ymlKey)) {
                break;
            }
            // If the tag is not a CompoundTag, this is the value we need.
            // TODO: Make it so if a value "exists" it is true, not just that the value is set to a specific one.
            if (!tag.contains(ymlKey, 10)) {
                Tag currentTag = tag.get(ymlKey);
                assert currentTag != null; // Guaranteed, we checked.
                value = currentTag.getAsString();
                section = section.getConfigurationSection(ymlKey);
                break;
            }
            // Otherwise, go deeper...
            section = section.getConfigurationSection(ymlKey);
            tag = tag.getCompound(ymlKey);
            assert section != null; // Guaranteed, ymlKey was pulled out of the keySet and the set was unmodified.
        }
        if (value == null) return;
        assert section != null; // Guaranteed, ymlKey was pulled out of the keySet and the set was unmodified.
        meta.setCustomModelData(section.getInt(value, meta.getCustomModelData()));
    }

    private boolean surprise(EntityType type, ItemMeta meta, CompoundTag tag) {
        ConfigurationSection section = texture.getConfigurationSection("special");
        if (section == null) return false;
        if (type == EntityType.CHICKEN && tag.getShort("Fire") > 0)  {
            meta.setCustomModelData(section.getInt("fried", 0));
            return true;
        }
        // Dog having owner workaround. TODO: Be smarter and figure a solution.
        if (type == EntityType.WOLF && tag.hasUUID("Owner")) {
            meta.setCustomModelData(section.getInt("tamed_wolf", 0));
            return true;
        }
        // Toast Rabbit workaround.
        /* TODO: Fix Toast Rabbit Workaround or be smarter and figure a solution.
        if (type == EntityType.RABBIT) {
            System.out.println("RABBIT");
            Tag name =  tag.get("CustomName");
            if (name != null) {
                System.out.println("RABBIT NAME");
                 Component nameComponent = SimpleBucketMobs.getGsonSerializer().deserialize(name.toString());
                 if (SimpleBucketMobs.getMiniMessage().stripTags(nameString).equals("Toast")) {
                     System.out.println("RABBIT NAME TOAST");
                     meta.setCustomModelData(section.getInt("toast", 0));
                     return true;
                 }
            }
        }
        */
        return false;
    }

}
