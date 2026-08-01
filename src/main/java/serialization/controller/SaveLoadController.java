package serialization.controller;

import serialization.model.LoadedGameData;
import serialization.service.LoadGameService;
import serialization.service.SaveGameService;
import player.model.PlayerModel;
import item.controller.ItemController;
import map.controller.MapController;

import java.io.IOException;
import java.util.List;

/**
 * Controlling class for every save/load game related action. View classes
 * go through this controller instead of talking to the Serialization
 * services directly.
 *
 * @author Vasilis Triantaris
 */
public class SaveLoadController {

    private final SaveGameService saveGameService = new SaveGameService();
    private final LoadGameService loadGameService = new LoadGameService();

    public void saveGame(PlayerModel player, MapController mapController, ItemController itemController) {
        saveGameService.savePlayerData(player, mapController, itemController);
    }

    public LoadedGameData loadGame(String fileName) throws IOException, ClassNotFoundException {
        return loadGameService.loadGameFileData(fileName);
    }

    public List<String> getSavedFileNames() {
        return loadGameService.getSavedFileNames();
    }

    public boolean playerNameExistsAsASaveFile(String playerName) {
        return loadGameService.playerNameExistsAsASaveFile(playerName);
    }

    public String deleteSavedGame(String fileName) {
        return loadGameService.deleteSavedGame(fileName);
    }
}