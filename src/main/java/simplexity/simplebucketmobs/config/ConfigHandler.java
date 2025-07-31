package simplexity.simplebucketmobs.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import simplexity.simplebucketmobs.SimpleBucketMobs;
import simplexity.simplebucketmobs.listener.BucketRule;
import simplexity.simplebucketmobs.util.Message;

import java.util.HashMap;
import java.util.Set;

public class ConfigHandler {

    private static ConfigHandler instance;

    // These can already be bucketed, we don't wanna double-bucket lol
    private final Set<EntityType> generalDisallowedTypes = Set.of(
            EntityType.TROPICAL_FISH, EntityType.SALMON, EntityType.COD,
            EntityType.AXOLOTL, EntityType.PUFFERFISH, EntityType.TADPOLE);

    private final HashMap<EntityType, BucketRule> entityRules = new HashMap<>();
    private BucketRule defaultRule;
    private String bucketTitle;
    private boolean useResourcePack, enchantmentGlint;
    // TODO: Disallowed Attributes

    public static ConfigHandler getInstance() {
        if (instance == null) instance = new ConfigHandler();
        return instance;
    }

    public String getBucketTitle() {
        return bucketTitle;
    }

    public void reloadConfig() {
        SimpleBucketMobs.getPlugin().reloadConfig();
        FileConfiguration config = SimpleBucketMobs.getPlugin().getConfig();
        bucketTitle = config.getString("bucket-style.title", "<aqua><type> in a Bucket");
        enchantmentGlint = config.getBoolean("bucket-style.enchantment-glint", true);
        useResourcePack = config.getBoolean("bucket-style.use-resource-pack", true);
        setupTypes(config);
    }

    private void setupTypes(FileConfiguration config) {
        boolean defaultAllowed = config.getBoolean("bucket-type-settings.default.allow", false);
        boolean defaultSneak = config.getBoolean("bucket-type-settings.default.sneak-required", false);
        boolean defaultPickupAggro = config.getBoolean("bucket-type-settings.default.pickup-when-aggro", false);
        boolean defaultRequiresPermission = config.getBoolean("bucket-type-settings.default.requires-permission", false);
        defaultRule = new BucketRule(defaultAllowed, defaultSneak, defaultPickupAggro, defaultRequiresPermission);
        ConfigurationSection typesSection = config.getConfigurationSection("bucket-type-settings.types");
        if (typesSection == null) return;
        for (EntityType disallowedEntity : generalDisallowedTypes) {
            entityRules.put(disallowedEntity, new BucketRule());
        }
        entityRules.clear();
        for (String entityKey : typesSection.getKeys(false)) {
            EntityType entityType = validateType(entityKey);
            if (entityType == null) continue;
            if (generalDisallowedTypes.contains(entityType)) continue;
            ConfigurationSection entitySection = typesSection.getConfigurationSection(entityKey);
            if (entitySection == null) {
                entityRules.put(entityType, new BucketRule(defaultAllowed, defaultSneak, defaultPickupAggro, defaultRequiresPermission));
                continue;
            }
            boolean allow = entitySection.getBoolean("allow", defaultAllowed);
            boolean sneak = entitySection.getBoolean("sneak-required", defaultSneak);
            boolean aggro = entitySection.getBoolean("pickup-when-aggro", defaultPickupAggro);
            boolean reqPerm = entitySection.getBoolean("requires-permission", defaultRequiresPermission);
            entityRules.put(entityType, new BucketRule(allow, sneak, aggro, reqPerm));
        }
    }


    private EntityType validateType(String entityName) {
        try {
            return EntityType.valueOf(entityName.toUpperCase());
        } catch (IllegalArgumentException e) {
            SimpleBucketMobs.getPlugin().getLogger().warning(Message.LOGGER_INVALID_MOB_TYPE.getMessage() + entityName);
        }
        return null;
    }


    public boolean isUsingResourcePack() {
        return useResourcePack;
    }

    private BucketRule getRule(EntityType type) {
        return entityRules.getOrDefault(type, defaultRule);
    }

    public boolean bucketingAllowed(EntityType type){
        return getRule(type).isAllowed();
    }

    public boolean isSneakRequired(EntityType type){
        return getRule(type).isSneakRequired();
    }

    public boolean canPickupWhenAggro(EntityType type){
        return getRule(type).canPickupWhenAggro();
    }

    public boolean requiresExplicitPermission(EntityType type){
        return getRule(type).shouldRequirePermission();
    }
}
