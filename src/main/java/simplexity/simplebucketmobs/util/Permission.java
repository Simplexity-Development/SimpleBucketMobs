package simplexity.simplebucketmobs.util;

public enum Permission {

    COMMAND_DEBUCKET("simplebucketmobs.debucket"),
    COMMAND_RELOAD("simplebucketmobs.reload"),

    BUCKET_MOB("simplebucketmobs.bucket."),
    BUCKET_ALL("simplebucketmobs.bucket.all");

    final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String get() { return this.permission; }

}
