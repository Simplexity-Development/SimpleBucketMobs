package simplexity.simplebucketmobs.util;

import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class BucketMobPermission {

    public static final Permission DEBUCKET_COMMAND = new Permission("simplebucketmobs.debucket", "Dump saved mob NBT data from Mob Bucket to chat.", PermissionDefault.OP);
    public static final Permission COMMAND_RELOAD = new Permission("simplebucketmobs.reload", "Reload plugin configuration files.", PermissionDefault.OP);

    public static final Permission BUCKET_MOB_BASE = new Permission("simplebucketmobs.use", "Allows the user to bucket the specified mob.", PermissionDefault.TRUE);

    public static Permission getBucketMobPermission(EntityType type){
        String permName = BUCKET_MOB_BASE + "." + type.toString().toLowerCase();
        return new Permission(permName, PermissionDefault.OP);
    }
}
