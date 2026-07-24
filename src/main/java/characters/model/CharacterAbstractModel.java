package characters.model;

import map.model.AreaModel;
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
    private AreaModel location;

    // Stats
    private int damage;
    private int armor;
    private int health;

    // Items
    private double gold;
}
