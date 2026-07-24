package map.enums;

import lombok.Getter;

@Getter
public enum DockYardMessagesEnum {
    THE_WAY_IS_UNREACHABLE("The way is unreachable!"),
    INSUFFICIENT_GOLD_FOR_TRAVEL("You don't have enough gold coins to travel to %s"),
    NO_SUCH_DESTINATION("There is no such destination!"),
    CANNOT_SINK_SHIP("You cannot sink the ship!"),
    BEAM_BLOCKING_SHIP("You cannot procced further, the beam is blocking the ship!");

    private final String message;

    DockYardMessagesEnum(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
