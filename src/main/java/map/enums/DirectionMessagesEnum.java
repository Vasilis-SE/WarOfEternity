package map.enums;

import lombok.Getter;

@Getter
public enum DirectionMessagesEnum {
    GATE_BLOCKING_PATH("You cannot proceed further. The gate is blocking your path!"),
    NO_DIRECTION_TO_DESTINATION("There is no direction to : %s");

    private final String message;

    DirectionMessagesEnum(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
