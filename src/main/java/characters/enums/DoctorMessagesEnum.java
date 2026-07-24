package characters.enums;

import lombok.Getter;

@Getter
public enum DoctorMessagesEnum {
    CANNOT_AFFORD_HEALING("Iam sorry sir, you dont have enough money for me to heal you..."),
    FULL_HEALTH("You don't have a single scratch!"),
    HEALING_SUCCESS("The doctor healed %s health points, for 10 gold coins!");

    private final String message;

    DoctorMessagesEnum(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
