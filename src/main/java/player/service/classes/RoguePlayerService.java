package player.service.classes;

import map.model.AreaModel;
import player.enums.PlayerClassesEnum;
import player.interfaces.PlayerInterface;
import player.model.PlayerAttributeStatsModel;
import player.model.PlayerModel;

import java.util.ArrayList;

public class RoguePlayerService implements PlayerInterface {

    private static final int STARTING_STRENGTH = 6;
    private static final int STARTING_AGILITY = 14;
    private static final int STARTING_INTELLIGENCE = 8;

    @Override
    public PlayerClassesEnum getPlayerClass() {
        return PlayerClassesEnum.ROGUE;
    }

    @Override
    public PlayerModel initPlayer(String name, AreaModel startingAreaModel) {
        return PlayerModel.builder()
                .playerClass(getPlayerClass())
                .name(name)
                .location(startingAreaModel)
                .health(100)
                .experience(0)
                .level(1)
                .strength(STARTING_STRENGTH)
                .agility(STARTING_AGILITY)
                .intelligence(STARTING_INTELLIGENCE)
                .inventory(new ArrayList<>())
                .equippedItems(new ArrayList<>())
                .build();
    }

    @Override
    public PlayerAttributeStatsModel getStartingAttributeStats() {
        return new PlayerAttributeStatsModel(STARTING_STRENGTH, STARTING_AGILITY, STARTING_INTELLIGENCE);
    }

    @Override
    public int calculateDamage(PlayerModel player, int equippedItemDamage) {
        return (int) ((equippedItemDamage * 0.3) + (player.getAgility() * 0.6) + (player.getLevel() * 0.1));
    }

    @Override
    public void applyLevelUpAttributeGrowth(PlayerModel player, PlayerAttributeStatsModel startingStats, PlayerAttributeStatsModel itemAttributeBonuses) {
        int level = player.getLevel();

        player.setStrength(startingStats.getStrength() + itemAttributeBonuses.getStrength() + (level - 1));
        player.setAgility(startingStats.getAgility() + itemAttributeBonuses.getAgility() + ((level * 2) - 2));
        player.setIntelligence(startingStats.getIntelligence() + itemAttributeBonuses.getIntelligence() + (level - 1));
    }
}
