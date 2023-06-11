package simplexity.simplebucketmobs.config;

import simplexity.simplebucketmobs.SimpleBucketMobs;
import simplexity.simplebucketmobs.util.Message;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class Locale {

    private static Locale instance;

    private final String fileName = "locale.yml";
    private final File dataFile = new File(SimpleBucketMobs.getPlugin().getDataFolder(), fileName);
    private final FileConfiguration locale = new YamlConfiguration();

    private Locale() {
        if (!dataFile.exists()) SimpleBucketMobs.getPlugin().saveResource(fileName, false);
        reloadLocale();
    }

    public static Locale getInstance() {
        if (instance == null) instance = new Locale();
        return instance;
    }

    public FileConfiguration getLocale() { return locale; }

    public void reloadLocale() {
        try { locale.load(dataFile); }
        catch (IOException | InvalidConfigurationException e) { e.printStackTrace(); }
        Set<String> keys = locale.getKeys(false);
        for (String key : keys) {
            try {
                Message message = Message.valueOf(key);
                message.setMessage(locale.getString(key, message.getMessage()));
            } catch (IllegalArgumentException e) {
                SimpleBucketMobs.getPlugin().getLogger().warning(Message.LOGGER_INVALID_LOCALE_KEY + key);
            }
        }
    }
}
