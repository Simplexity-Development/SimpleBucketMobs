package simplexity.simplebucketmobs.util;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class BucketMobPermission {

    public static final Permission DEBUCKET_COMMAND = new Permission("simplebucketmobs.debucket", "Dump saved mob NBT data from Mob Bucket to chat.", PermissionDefault.OP);
    public static final Permission COMMAND_RELOAD = new Permission("simplebucketmobs.reload", "Reload plugin configuration files.", PermissionDefault.OP);

    public static final Permission BUCKET_MOB_BASE = new Permission("simplebucketmobs.bucket.", "Allows the user to bucket the specified mob.", PermissionDefault.OP);
    public static final Permission BUCKET_ALL = new Permission("simplebucketmobs.bucket.all", "Allows the user to bucket all enabled mobs.", PermissionDefault.OP);
}
