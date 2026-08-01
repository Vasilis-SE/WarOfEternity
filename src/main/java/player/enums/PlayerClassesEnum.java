package player.enums;

import lombok.Getter;

@Getter
public enum PlayerClassesEnum {
    WARRIOR("Warrior"),
    ROGUE("Rogue"),
    MAGE("Mage");

    private final String label;

    PlayerClassesEnum(String label) {
        this.label = label;
    }
}
