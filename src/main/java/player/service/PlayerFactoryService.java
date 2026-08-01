package player.service;

import player.enums.PlayerClassesEnum;
import player.interfaces.PlayerInterface;
import player.service.classes.MagePlayerService;
import player.service.classes.RoguePlayerService;
import player.service.classes.WarriorPlayerService;

import java.util.EnumMap;
import java.util.Map;

/**
 * Factory that resolves the {@link PlayerInterface} implementation
 * responsible for a given {@link PlayerClassesEnum}.
 */
public class PlayerFactoryService {

    private final Map<PlayerClassesEnum, PlayerInterface> playerClassServices;

    public PlayerFactoryService() {
        this.playerClassServices = new EnumMap<>(PlayerClassesEnum.class);
        this.playerClassServices.put(PlayerClassesEnum.WARRIOR, new WarriorPlayerService());
        this.playerClassServices.put(PlayerClassesEnum.ROGUE, new RoguePlayerService());
        this.playerClassServices.put(PlayerClassesEnum.MAGE, new MagePlayerService());
    }

    public PlayerInterface getPlayer(PlayerClassesEnum playerClass) {
        PlayerInterface service = playerClassServices.get(playerClass);

        if (service == null)
            throw new IllegalArgumentException("No PlayerClassService registered for player class: " + playerClass);

        return service;
    }
}
