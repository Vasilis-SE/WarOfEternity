package characters.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class EnemyModel extends CharacterAbstractModel implements Serializable {
    private String description;
    private String image;
    private int experience;
    private int encounterPercent;
}
