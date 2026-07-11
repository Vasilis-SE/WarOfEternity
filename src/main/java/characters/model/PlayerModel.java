package characters.model;

import Items.Item;
import characters.enums.PlayerClassesEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class PlayerModel extends CharacterAbstractModel implements Serializable {
    private PlayerClassesEnum playerClass;

    private int strength;
    private int intelligence;
    private int agility;

    private int level;
    private int experience;

    private List<Item> inventory;
    private List<Item> equippedItems;
}
