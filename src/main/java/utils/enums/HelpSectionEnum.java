package utils.enums;

import lombok.Getter;

@Getter
public enum HelpSectionEnum {
    COMMANDS("Commands"),
    RECOGNITION("Recognition"),
    NPCS("NPC's"),
    EXPERIENCE_MECHANISM("Experience Mechanism"),
    ATTRIBUTE_MECHANISM("Attribute Mechanism"),
    GAME_MAP("Game Map"),
    MUSIC("Music");

    private final String title;

    HelpSectionEnum(String title) {
        this.title = title;
    }
}
