package simplexity.simplebucketmobs.listener;

public class BucketRule {
    private final boolean allow;
    private final boolean sneakRequired;
    private final boolean pickupWhenAggro;
    private final boolean requiresPermission;

    public BucketRule(){
        this.allow = false;
        this.sneakRequired = false;
        this.pickupWhenAggro = false;
        this.requiresPermission = false;
    }

    public BucketRule(boolean allow, boolean sneakRequired, boolean pickupWhenAggro, boolean requiresPermission){
        this.allow = allow;
        this.sneakRequired = sneakRequired;
        this.pickupWhenAggro = pickupWhenAggro;
        this.requiresPermission = requiresPermission;
    }

    public boolean isAllowed() {
        return allow;
    }

    public boolean isSneakRequired() {
        return sneakRequired;
    }

    public boolean canPickupWhenAggro() {
        return pickupWhenAggro;
    }

    public boolean shouldRequirePermission() {
        return requiresPermission;
    }
}
