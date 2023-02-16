package adhdmc.simplebucketmobs.config;

import adhdmc.simplebucketmobs.SimpleBucketMobs;
import adhdmc.simplebucketmobs.util.Message;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Config {

    private static Config instance;

    private final Set<EntityType> allowedTypes;
    private String bucketTitle;
    // TODO: Allowed Types dependent on Bucket
    // TODO: Disallowed Attributes

    private Config() {
        allowedTypes = new HashSet<>();
    }

    public static Config getInstance() {
        if (instance == null) instance = new Config();
        return instance;
    }

    public Set<EntityType> getAllowedTypes() { return Collections.unmodifiableSet(allowedTypes); }
    public String getBucketTitle() { return bucketTitle; }

    public void reloadConfig() {
        SimpleBucketMobs.getPlugin().reloadConfig();
        FileConfiguration config = SimpleBucketMobs.getPlugin().getConfig();
        List<String> types = config.getStringList("allowed-types");
        allowedTypes.clear();
        for (String type : types) {
            try {
                EntityType entityType = EntityType.valueOf(type.toUpperCase());
                allowedTypes.add(entityType);
            }
            catch (IllegalArgumentException e) {
                SimpleBucketMobs.getPlugin().getLogger().warning(Message.LOGGER_INVALID_MOB_TYPE.getMessage() + type);
            }
        }
        bucketTitle = config.getString("bucket-title", "<aqua><type> Bucket");
    }

}
