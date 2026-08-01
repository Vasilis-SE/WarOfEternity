package player.interfaces;

import map.model.AreaModel;
import player.enums.PlayerClassesEnum;
import player.model.PlayerAttributeStatsModel;
import player.model.PlayerModel;
import player.service.PlayerFactoryService;

/**
 * Encapsulates every piece of behaviour that differs from one player class
 * to another (starting stats, damage formula, level-up attribute growth).
 * Implemented once per {@link PlayerClassesEnum} value and resolved through
 * {@link PlayerFactoryService}.
 */
public interface PlayerInterface {

    PlayerClassesEnum getPlayerClass();

    /**
     * Builds a brand-new player of this class, with its starting stats,
     * location and empty inventory already set.
     */
    PlayerModel initPlayer(String name, AreaModel startingAreaModel);

    /**
     * The base strength/agility/intelligence points this class starts with.
     */
    PlayerAttributeStatsModel getStartingAttributeStats();

    /**
     * Computes the player's total damage from the class' primary attribute,
     * the player's level and the damage contributed by equipped items.
     */
    int calculateDamage(PlayerModel player, int equippedItemDamage);

    /**
     * Applies this class' per-level attribute growth (the primary attribute
     * grows twice as fast as the other two) on top of the starting stats and
     * the bonus points granted by equipped items.
     */
    void applyLevelUpAttributeGrowth(PlayerModel player, PlayerAttributeStatsModel startingStats, PlayerAttributeStatsModel itemAttributeBonuses);
}
