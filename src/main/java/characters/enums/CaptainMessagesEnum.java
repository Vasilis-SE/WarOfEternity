package characters.enums;

import lombok.Getter;

@Getter
public enum CaptainMessagesEnum {
    NO_DOCKYARD_IN_AREA("There is no dockyard in this place!"),
    SAIL_OFFER("I can get you to %s for %s gold coins.");

    private final String message;

    CaptainMessagesEnum(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
