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

    // These can already be bucketed, we don't wanna double-bucket lol
    private final Set<EntityType> generalDisallowedTypes = Set.of(
            EntityType.TROPICAL_FISH, EntityType.SALMON, EntityType.COD,
            EntityType.AXOLOTL, EntityType.PUFFERFISH, EntityType.TADPOLE);

    private final Set<EntityType> allowedBasicTypes;
    private String bucketTitle;
    private boolean noHostileTargeting;
    private boolean listIsBlacklist;
    private boolean useResourcePack;
    // TODO: Disallowed Attributes

    private Config() {
        allowedBasicTypes = new HashSet<>();
    }

    public static Config getInstance() {
        if (instance == null) instance = new Config();
        return instance;
    }

    public Set<EntityType> getAllowedBasicTypes() {
        return Collections.unmodifiableSet(allowedBasicTypes);
    }

    public String getBucketTitle() {
        return bucketTitle;
    }

    public boolean isNoHostileTargeting() {
        return noHostileTargeting;
    }

    public void reloadConfig() {
        SimpleBucketMobs.getPlugin().reloadConfig();
        FileConfiguration config = SimpleBucketMobs.getPlugin().getConfig();
        bucketTitle = config.getString("bucket-title", "<aqua><type> Bucket");
        noHostileTargeting = config.getBoolean("no-hostile-targeting", true);
        listIsBlacklist = config.getBoolean("list-is-blacklist", false);
        useResourcePack = config.getBoolean("use-resource-pack", true);
        setupTypes(config);
    }

    private void setupTypes(FileConfiguration config) {
        List<String> basicTypes = config.getStringList("allowed-types");
        allowedBasicTypes.clear();
        if (listIsBlacklist) {
            populateTypes(basicTypes, allowedBasicTypes);
        } else {
            validateTypes(basicTypes, allowedBasicTypes);
        }
    }

    private void validateTypes(List<String> stringList, Set<EntityType> entityList) {
        for (String type : stringList) {
            try {
                EntityType entityType = EntityType.valueOf(type.toUpperCase());
                entityList.add(entityType);
            } catch (IllegalArgumentException e) {
                SimpleBucketMobs.getPlugin().getLogger().warning(Message.LOGGER_INVALID_MOB_TYPE.getMessage() + type);
            }
        }
    }

    private void populateTypes(List<String> stringList, Set<EntityType> entityList) {
        for (EntityType entityType : EntityType.values()) {
            if (stringList.contains(entityType.toString())) continue;
            if (generalDisallowedTypes.contains(entityType)) continue;
            entityList.add(entityType);
        }
    }

    public boolean isUseResourcePack() {
        return useResourcePack;
    }
}
