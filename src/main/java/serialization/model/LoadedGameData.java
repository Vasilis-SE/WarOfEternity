package serialization.model;

import characters.model.PlayerModel;
import item.controller.ItemController;
import lombok.AllArgsConstructor;
import lombok.Getter;
import map.controller.MapController;

/**
 * Holds the three objects restored from a save file.
 *
 * @author Vasilis Triantaris
 */
@Getter
@AllArgsConstructor
public class LoadedGameData {

    private final PlayerModel player;
    private final MapController mapController;
    private final ItemController itemController;
}