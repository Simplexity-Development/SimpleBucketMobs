package simplexity.simplebucketmobs.config;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.slf4j.Logger;
import simplexity.simplebucketmobs.SimpleBucketMobs;
import simplexity.simplebucketmobs.listener.BucketRule;

import java.util.HashMap;
import java.util.Set;

public class ConfigHandler {

    private static ConfigHandler instance;
    private static final Logger logger = SimpleBucketMobs.getPlugin().getSLF4JLogger();

    // These can already be bucketed, we don't wanna double-bucket lol
    private final Set<EntityType> generalDisallowedTypes = Set.of(
            EntityType.TROPICAL_FISH, EntityType.SALMON, EntityType.COD,
            EntityType.AXOLOTL, EntityType.PUFFERFISH, EntityType.TADPOLE);

    private final HashMap<EntityType, BucketRule> entityRules = new HashMap<>();
    private BucketRule defaultRule;
    private String bucketTitle;
    private NamespacedKey defaultModel;
    private boolean useResourcePack, enchantmentGlint;
    private Material mobBucketMaterial;
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
        bucketTitle = config.getString("captured-item-style.default.name", "<aqua><display_name> in a Bucket");
        enchantmentGlint = config.getBoolean("captured-item-style.default.enchantment-glint", true);
        useResourcePack = config.getBoolean("captured-item-style.use-resource-pack", true);
        mobBucketMaterial = validateMaterial(config.getString("captured-item-style.default.item-type"), Material.BUCKET, "captured-item-style.default.item-type");
        defaultModel = validateItemModel(config.getString("captured-item-style.default.item-model", "minecraft:bucket"));
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
            logger.warn("Invalid entity type: {} - settings for this entity will be skipped", entityName);
        }
        return null;
    }

    private NamespacedKey validateItemModel(String modelLocation){
        NamespacedKey key = NamespacedKey.fromString(modelLocation);
        if (key == null) {
            logger.warn("Invalid item model: {} - using default model 'minecraft:bucket'", modelLocation);
            return NamespacedKey.fromString("minecraft:bucket");
        }
        return key;
    }

    @SuppressWarnings("SameParameterValue")
    private Material validateMaterial(String materialName, Material defaultMaterial, String path) {
        if (materialName == null) {
            logger.warn("No material found for {}, using default material: {}", path, defaultMaterial);
            return defaultMaterial;
        }
        Material material = Material.getMaterial(materialName);
        if (material == null) {
            logger.warn("Invalid material in '{}': {} - using default material: {}", path, materialName, defaultMaterial);
            return defaultMaterial;
        }
        return material;
    }


    public boolean isUsingResourcePack() {
        return useResourcePack;
    }

    private BucketRule getRule(EntityType type) {
        return entityRules.getOrDefault(type, defaultRule);
    }

    public boolean bucketingAllowed(EntityType type) {
        return getRule(type).isAllowed();
    }

    public boolean isSneakRequired(EntityType type) {
        return getRule(type).isSneakRequired();
    }

    public boolean canPickupWhenAggro(EntityType type) {
        return getRule(type).canPickupWhenAggro();
    }

    public boolean requiresExplicitPermission(EntityType type) {
        return getRule(type).shouldRequirePermission();
    }

    public boolean isEnchantmentGlint() {
        return enchantmentGlint;
    }

    public Material getMobBucketMaterial() {
        return mobBucketMaterial;
    }

    public NamespacedKey getDefaultModel() {
        return defaultModel;
    }
}
