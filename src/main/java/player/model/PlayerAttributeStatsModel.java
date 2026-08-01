package player.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Holds a strength/agility/intelligence triplet. Used to carry a player
 * class' base attribute points and the bonus attribute points granted by
 * equipped items, without resorting to a generic map/JSON bag.
 */
@Getter
@AllArgsConstructor
public class PlayerAttributeStatsModel {
    private final int strength;
    private final int agility;
    private final int intelligence;
}
