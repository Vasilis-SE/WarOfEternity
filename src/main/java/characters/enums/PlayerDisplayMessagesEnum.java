package characters.enums;

import lombok.Getter;

@Getter
public enum PlayerDisplayMessagesEnum {
    INVENTORY_WEIGHT("%s / 100"),
    PLAYER_LEVEL("Level : %d");

    private final String message;

    PlayerDisplayMessagesEnum(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
