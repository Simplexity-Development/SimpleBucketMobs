package adhdmc.simplebucketmobs.util;

import adhdmc.simplebucketmobs.SimpleBucketMobs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public enum Message {

    PREFIX("<white>[<aqua>SimpleBucketMobs</aqua>]</white> <gray>»</gray> "),

    LOGGER_INVALID_LOCALE_KEY("Invalid Key in locale.yml: "),
    ERROR_NOT_A_PLAYER("<prefix><red>This command can only be executed by a player.</red>"),
    ERROR_NO_BUCKET_MOB("<prefix><red>This is not a bucket mob, please report this to a server admin.</red>"),
    ERROR_FAILED_DESERIALIZATION("<prefix><red>Failed to deserialize mob, please report this to a server admin.</red>");

    private String message;

    Message(String message) {
        this.message = message;
    }

    public void setMessage(String message) { this.message = message; }

    public String getMessage() { return this.message; }

    public Component getParsedMessage() {
        return SimpleBucketMobs.getMiniMessage().deserialize(
                message,
                Placeholder.unparsed("prefix", Message.PREFIX.getMessage())
        );
    }

}
