package simplexity.simplebucketmobs.util;

import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public enum BucketMobPermission {
    DEBUCKET_COMMAND("simplebucketmobs.debucket", "Display serialized mob data from a held Mob Bucket.", PermissionDefault.OP),
    COMMAND_RELOAD("simplebucketmobs.reload", "Reload plugin configuration files.", PermissionDefault.OP),
    BUCKET_MOB_BASE("simplebucketmobs.use", "Allows the user to bucket mobs.", PermissionDefault.TRUE);

    private final String node;
    private final Permission permission;

    BucketMobPermission(@NotNull String node, @NotNull String description, @NotNull PermissionDefault permissionDefault) {
        this.node = node;
        this.permission = new Permission(node, description, permissionDefault);
    }

    @NotNull
    public Permission getPermission() {
        return permission;
    }

    @NotNull
    public String getNode() {
        return node;
    }

    @NotNull
    public static Permission getBucketMobPermission(@NotNull EntityType type) {
        String permName = BUCKET_MOB_BASE.node + "." + type.toString().toLowerCase();
        return new Permission(permName, PermissionDefault.OP);
    }
}