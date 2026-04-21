package simplexity.simplebucketmobs.listener;

public class BucketRule {
    public final boolean allow;
    public final boolean sneakRequired;
    public final boolean pickupWhenAggro;
    public final boolean requiresPermission;

    public BucketRule(boolean allow, boolean sneakRequired, boolean pickupWhenAggro, boolean requiresPermission) {
        this.allow = allow;
        this.sneakRequired = sneakRequired;
        this.pickupWhenAggro = pickupWhenAggro;
        this.requiresPermission = requiresPermission;
    }
}