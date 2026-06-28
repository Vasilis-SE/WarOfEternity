package characters.model;

import Map.Area;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class CharacterAbstractModel {
    // General
    private String name;
    private Area location;

    // Stats
    private int damage;
    private int armor;
    private int health;

    // Items
    private double gold;
}
