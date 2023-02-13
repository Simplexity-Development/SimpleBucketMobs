package adhdmc.simplebucketmobs.config;

import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Config {

    private static Config instance;

    private Set<EntityType> allowedTypes;
    // TODO: Disallowed Attributes

    private Config() {
        allowedTypes = new HashSet<>();
    }

    public static Config getInstance() {
        if (instance == null) instance = new Config();
        return instance;
    }

    public Set<EntityType> getAllowedTypes() { return Collections.unmodifiableSet(allowedTypes); }

}
