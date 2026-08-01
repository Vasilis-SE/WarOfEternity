package serialization.service;

import player.model.PlayerModel;
import item.controller.ItemController;
import map.controller.MapController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service that serializes the current game state (player, map and items) to
 * a {@code .sav} file inside the saves folder.
 *
 * @author Vasilis Triantaris
 */
public class SaveGameService {

    public void savePlayerData(PlayerModel player, MapController mapController, ItemController itemController) {
        String saveFilePath = SaveFilePathResolver.resolveSaveFilePath(player.getName() + ".sav");

        try (FileOutputStream fileOut = new FileOutputStream(saveFilePath);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

            out.writeObject(player);
            out.writeObject(mapController);
            out.writeObject(itemController);
        } catch (IOException ex) {
            Logger.getLogger(SaveGameService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}