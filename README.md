# SimpleBucketMobs

[![wakatime](https://wakatime.com/badge/user/1f3b44b5-611a-484d-bcdc-b2084eefec1a/project/2a7588f5-f1d8-4dae-aa7b-fea0bf437f78.svg)](https://wakatime.com/@1f3b44b5-611a-484d-bcdc-b2084eefec1a/projects/szguvxhtkl)

Throw mobs into buckets - like axolotls, but for everything else.

Right-click a mob with an empty bucket to capture it. Right-click a block to release it.

## Commands

| Command | Description | Permission |
|---|---|---|
| `/sbm reload` | Reload all configuration files | `simplebucketmobs.reload` |
| `/sbm debucket` | Print NBT debug info for the bucket mob in your hand | `simplebucketmobs.debucket` |

Aliases: `/simplebucketmobs`, `/simplebucketmob`

## Permissions

| Permission | Description | Default |
|---|---|---|
| `simplebucketmobs.bucket.<mob>` | Bucket a specific mob type (e.g. `simplebucketmobs.bucket.sheep`) | `false` |
| `simplebucketmobs.bucket.all` | Bucket all enabled mob types | `op` |
| `simplebucketmobs.reload` | Reload configuration files | `op` |
| `simplebucketmobs.debucket` | Dump saved mob data to chat | `op` |

Per-mob permissions are only checked when `requires-permission: true` is set for that mob in `config.yml`.

## Configuration (`config.yml`)

### Captured Item Style

Controls the appearance of the bucket item produced when a mob is captured.

```yaml
captured-item-style:
  use-resource-pack: true       # Apply item-model from texture.yml (requires a resource pack)
  default:
    item-type: BUCKET           # Material of the bucket item
    item-model: "minecraft:bucket"  # Fallback item model (NamespacedKey)
    name: "<aqua><display_name> in a Bucket"  # MiniMessage display name
    lore:
      - "<gray>A <type_name_cased> sits in the bucket</gray>"
    enchantment-glint: true     # Whether the item has an enchantment glint
```

**Name placeholders:**

| Placeholder | Example output |
|---|---|
| `<type>` | `SHEEP` |
| `<type_name_cased>` | `Sheep` |
| `<display_name>` | Custom name if set, otherwise the mob's default display name |

### Mob Rules (`bucket-type-settings`)

Controls which mobs can be bucketed and under what conditions.

```yaml
bucket-type-settings:
  default:
    allow: false
    sneak-required: false
    pickup-when-aggro: false
    requires-permission: false
  types:
    SHEEP:
      allow: true
    VILLAGER:
      allow: true
      sneak-required: true       # Player must be sneaking to bucket this mob
      requires-permission: true  # Checks simplebucketmobs.bucket.villager
    SLIME:
      allow: true
      pickup-when-aggro: true    # Can be bucketed even when targeting the player
```

Valid entity type names follow the [Bukkit EntityType enum](https://purpurmc.org/javadoc/org/bukkit/entity/EntityType.html). The following are always blocked regardless of config, since they are already bucketable in vanilla: `TROPICAL_FISH`, `SALMON`, `COD`, `AXOLOTL`, `PUFFERFISH`, `TADPOLE`.

## Texture Configuration (`texture.yml`)

`texture.yml` maps mob types (and their variants) to item model keys. These keys are [NamespacedKeys](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/NamespacedKey.html) pointing to models in your resource pack. `use-resource-pack` in `config.yml` must be `true` for these to apply.

The default values reference the [Bucket Mobs Resources](https://modrinth.com/resourcepack/bucket-mobs-resources/versions) resource pack.

### Basic format

Every mob entry supports a `default` key used as the fallback when no variant matches:

```yaml
blaze:
  default: "simplexity:bucket_mobs/misc/blaze"
```

### Variant format

For mobs whose variant is recognized by the plugin (see table below), sub-keys under `type` are matched against the variant name:

```yaml
frog:
  default: "simplexity:bucket_mobs/frog/cold"
  type:
    cold: "simplexity:bucket_mobs/frog/cold"
    temperate: "simplexity:bucket_mobs/frog/temperate"
    warm: "simplexity:bucket_mobs/frog/warm"
```

### Supported variant lookups

| Mob | `type` key format | Example keys |
|---|---|---|
| Cat | `Cat.Type` enum, lowercase | `tabby`, `black`, `red`, `siamese`, `british_shorthair`, `calico`, `persian`, `ragdoll`, `white`, `jellie`, `all_black` |
| Fox | `Fox.Type` enum, lowercase | `red`, `snow` |
| Frog | `Frog.Variant` enum, lowercase | `cold`, `temperate`, `warm` |
| Horse | `<color>_<style>`, both lowercase | `brown_none`, `white_white_stockings`, `black_white_field`, etc. |
| Llama | `Llama.Color` enum, lowercase | `creamy`, `white`, `brown`, `gray` |
| Mooshroom | `MushroomCow.Variant` enum, lowercase | `red`, `brown` |
| Parrot | `Parrot.Variant` enum, lowercase | `red`, `blue`, `green`, `cyan`, `gray` |
| Rabbit | `Rabbit.Type` enum, lowercase | `brown`, `white`, `black`, `black_and_white`, `gold`, `salt_and_pepper`, `the_killer_bunny` |
| Sheep | `DyeColor` enum, lowercase | `white`, `orange`, `black`, `red`, etc. |
| Shulker | `DyeColor` enum, lowercase | `white`, `orange`, `black`, `red`, etc. |
| Trader Llama | `Llama.Color` enum, lowercase | `creamy`, `white`, `brown`, `gray` |
| Villager | `Villager.Profession` enum, lowercase | `none`, `farmer`, `librarian`, `cleric`, etc. |
| Wolf | `Wolf.Variant` enum, lowercase | `pale`, `ashen`, `black`, `chestnut`, `rusty`, `snowy`, `spotted`, `striped`, `woods` |

All other mobs use only `default`.

### Horse key format

Horse keys combine color and marking style separated by an underscore:

```yaml
horse:
  type:
    brown_none: "simplexity:bucket_mobs/horse/brown_default"
    brown_white_stockings: "simplexity:bucket_mobs/horse/brown_white_stockings_and_blaze"
    brown_white_field: "simplexity:bucket_mobs/horse/brown_white_field"
```

Valid colors: `white`, `creamy`, `chestnut`, `brown`, `black`, `gray`, `dark_brown`  
Valid styles: `none`, `white`, `whitefield`, `white_stockings`, `dots`, `black_dots`

## Locale (`locale.yml`)

All player-facing messages are defined in `locale.yml` using [MiniMessage](https://docs.advntr.dev/minimessage/format.html) syntax. Edit the values there to change any message text without recompiling.