package simplexity.simplebucketmobs.config;

import simplexity.simplebucketmobs.SimpleBucketMobs;
import simplexity.simplebucketmobs.util.Message;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Config {

    private static Config instance;

    private final Set<EntityType> allowedBasicTypes;
    private String bucketTitle;
    private boolean noHostileTargeting;
    // TODO: Disallowed Attributes

    private Config() {
        allowedBasicTypes = new HashSet<>();
    }

    public static Config getInstance() {
        if (instance == null) instance = new Config();
        return instance;
    }

    public Set<EntityType> getAllowedBasicTypes() { return Collections.unmodifiableSet(allowedBasicTypes); }
    public String getBucketTitle() { return bucketTitle; }
    public boolean isNoHostileTargeting() { return noHostileTargeting; }

    public void reloadConfig() {
        SimpleBucketMobs.getPlugin().reloadConfig();
        FileConfiguration config = SimpleBucketMobs.getPlugin().getConfig();
        setupTypes(config);
        bucketTitle = config.getString("bucket-title", "<aqua><type> Bucket");
        noHostileTargeting = config.getBoolean("no-hostile-targeting", true);
    }

    private void setupTypes(FileConfiguration config) {
        List<String> basicTypes = config.getStringList("allowed-types");
        allowedBasicTypes.clear();
        validateTypes(basicTypes, allowedBasicTypes);
    }

    private void validateTypes(List<String> stringList, Set<EntityType> entityList){
        for (String type : stringList) {
            try {
                EntityType entityType = EntityType.valueOf(type.toUpperCase());
                entityList.add(entityType);
            }
            catch (IllegalArgumentException e) {
                SimpleBucketMobs.getPlugin().getLogger().warning(Message.LOGGER_INVALID_MOB_TYPE.getMessage() + type);
            }
        }
    }

}
