package simplexity.simplebucketmobs.config;

import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.slf4j.Logger;
import simplexity.simplebucketmobs.SimpleBucketMobs;

import java.io.File;
import java.io.IOException;

@SuppressWarnings({"CallToPrintStackTrace"})
public class Texture {

    private static Texture instance;

    private final String fileName = "texture.yml";
    private final File dataFile = new File(SimpleBucketMobs.getPlugin().getDataFolder(), fileName);
    private final FileConfiguration texture = new YamlConfiguration();
    private final Logger logger = SimpleBucketMobs.getPlugin().getSLF4JLogger();

    private Texture() {
        if (!dataFile.exists()) SimpleBucketMobs.getPlugin().saveResource(fileName, false);
        reloadTextureConfig();
    }

    public static Texture getInstance() {
        if (instance == null) instance = new Texture();
        return instance;
    }

    public FileConfiguration getTextureConfig() {
        return texture;
    }

    public void reloadTextureConfig() {
        try {
            texture.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public NamespacedKey getItemModel(LivingEntity entity) {
        String itemModelLocation = locateItemModel(entity);
        NamespacedKey key = NamespacedKey.fromString(itemModelLocation);
        if (key == null) {
            logger.warn("Invalid or missing item model string for entity: {} Value: '{}'", entity.getType(), itemModelLocation);
            return ConfigHandler.getInstance().getDefaultModel();
        }
        return key;
    }

    private String locateItemModel(LivingEntity entity) {
        if (entity instanceof Cat cat) return getCatItemModel(cat);
        if (entity instanceof Chicken chicken) return getChickenItemModel(chicken);
        if (entity instanceof Cow cow) return getCowItemModel(cow);
        if (entity instanceof Fox fox) return getFoxItemModel(fox);
        if (entity instanceof Frog frog) return getFrogItemModel(frog);
        if (entity instanceof Horse horse) return getHorseItemModel(horse);
        // Have to do this first otherwise llama will eat it
        if (entity instanceof TraderLlama traderLlama) return getTraderLlamaItemModel(traderLlama);
        if (entity instanceof Llama llama) return getLlamaItemModel(llama);
        if (entity instanceof MushroomCow mooshroom) return getMooshroomItemModel(mooshroom);
        if (entity instanceof Parrot parrot) return getParrotItemModel(parrot);
        if (entity instanceof Pig pig) return getPigItemModel(pig);
        if (entity instanceof Rabbit rabbit) return getRabbitItemModel(rabbit);
        if (entity instanceof Sheep sheep) return getSheepItemModel(sheep);
        if (entity instanceof Shulker shulker) return getShulkerItemModel(shulker);
        if (entity instanceof Villager villager) return getVillagerItemModel(villager);
        if (entity instanceof Wolf wolf) return getWolfItemModel(wolf);
        return texture.getString(entity.getType().toString().toLowerCase() + ".default", "minecraft:bucket");
    }

    public String getCatItemModel(Cat cat) {
        Cat.Type catType = cat.getCatType();
        String defaultTexture = texture.getString("cat.default", "minecraft:bucket");
        return texture.getString("cat.type." + catType.toString().toLowerCase(), defaultTexture);
    }

    public String getChickenItemModel(Chicken chicken) {
        Chicken.Variant chickenType = chicken.getVariant();
        String defaultTexture = texture.getString("chicken.default", "minecraft:bucket");
        return texture.getString("chicken.type." + chickenType.toString().toLowerCase(), defaultTexture);
    }

    public String getCowItemModel(Cow cow) {
        Cow.Variant cowType = cow.getVariant();
        String defaultTexture = texture.getString("cow.default", "minecraft:bucket");
        return texture.getString("cow.type." + cowType.toString().toLowerCase(), defaultTexture);
    }

    public String getFoxItemModel(Fox fox) {
        Fox.Type foxType = fox.getFoxType();
        String defaultTexture = texture.getString("fox.default", "minecraft:bucket");
        return texture.getString("fox.type." + foxType.toString().toLowerCase(), defaultTexture);
    }

    public String getFrogItemModel(Frog frog) {
        Frog.Variant frogType = frog.getVariant();
        String defaultTexture = texture.getString("frog.default", "minecraft:bucket");
        return texture.getString("frog.type." + frogType.toString().toLowerCase(), defaultTexture);
    }

    public String getHorseItemModel(Horse horse) {
        Horse.Color color = horse.getColor();
        Horse.Style style = horse.getStyle();
        String defaultTexture = texture.getString("horse.default", "minecraft:bucket");
        String colorKey = color.name().toLowerCase();
        String styleKey = style.name().toLowerCase();
        String key = colorKey + "_" + styleKey;
        return texture.getString("horse.type." + key, defaultTexture);
    }

    public String getLlamaItemModel(Llama llama) {
        Llama.Color llamaColor = llama.getColor();
        String defaultTexture = texture.getString("llama.default", "minecraft:bucket");
        return texture.getString("llama.type." + llamaColor.toString().toLowerCase(), defaultTexture);
    }

    public String getMooshroomItemModel(MushroomCow mooshroom) {
        MushroomCow.Variant mooshroomType = mooshroom.getVariant();
        String defaultTexture = texture.getString("mooshroom.default", "minecraft:bucket");
        return texture.getString("mooshroom.type." + mooshroomType.toString().toLowerCase(), defaultTexture);
    }

    public String getParrotItemModel(Parrot parrot) {
        Parrot.Variant parrotType = parrot.getVariant();
        String defaultTexture = texture.getString("parrot.default", "minecraft:bucket");
        return texture.getString("parrot.type." + parrotType.toString().toLowerCase(), defaultTexture);
    }

    public String getPigItemModel(Pig pig) {
        Pig.Variant pigType = pig.getVariant();
        String defaultTexture = texture.getString("pig.default", "minecraft:bucket");
        return texture.getString("pig.type." + pigType.toString().toLowerCase(), defaultTexture);
    }

    public String getRabbitItemModel(Rabbit rabbit) {
        Rabbit.Type rabbitType = rabbit.getRabbitType();
        String defaultTexture = texture.getString("rabbit.default", "minecraft:bucket");
        return texture.getString("rabbit.type." + rabbitType.toString().toLowerCase(), defaultTexture);
    }

    public String getSheepItemModel(Sheep sheep) {
        DyeColor sheepType = sheep.getColor();
        String defaultTexture = texture.getString("sheep.default", "minecraft:bucket");
        if (sheepType == null) return defaultTexture;
        return texture.getString("sheep.type." + sheepType.toString().toLowerCase(), defaultTexture);
    }

    public String getShulkerItemModel(Shulker shulker) {
        DyeColor shulkerType = shulker.getColor();
        String defaultTexture = texture.getString("shulker.default", "minecraft:bucket");
        if (shulkerType == null) return defaultTexture;
        return texture.getString("shulker.type." + shulkerType.toString().toLowerCase(), defaultTexture);
    }

    public String getTraderLlamaItemModel(TraderLlama traderLlama) {
        Llama.Color llamaColor = traderLlama.getColor();
        String defaultTexture = texture.getString("trader_llama.default", "minecraft:bucket");
        return texture.getString("trader_llama.type." + llamaColor.toString().toLowerCase(), defaultTexture);
    }

    public String getVillagerItemModel(Villager villager) {
        Villager.Profession villagerProfession = villager.getProfession();
        String defaultTexture = texture.getString("villager.default", "minecraft:bucket");
        return texture.getString("villager.type." + villagerProfession.toString().toLowerCase(), defaultTexture);
    }

    public String getWolfItemModel(Wolf wolf) {
        Wolf.Variant wolfType = wolf.getVariant();
        String defaultTexture = texture.getString("wolf.default", "minecraft:bucket");
        return texture.getString("wolf.type." + wolfType.toString().toLowerCase(), defaultTexture);
    }

}
