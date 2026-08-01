package serialization.service;

import serialization.enums.SaveLoadMessagesEnum;
import serialization.model.LoadedGameData;
import player.model.PlayerModel;
import item.controller.ItemController;
import map.controller.MapController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Service that restores a previously saved game and manages the save files
 * on disk (listing, existence check, deletion).
 *
 * @author Vasilis Triantaris
 */
public class LoadGameService {

    public LoadedGameData loadGameFileData(String fileSelected) throws IOException, ClassNotFoundException {
        String pathToSaveFile = SaveFilePathResolver.resolveSaveFilePath(fileSelected);

        try (FileInputStream fis = new FileInputStream(pathToSaveFile);
             ObjectInputStream in = new ObjectInputStream(fis)) {

            PlayerModel player = (PlayerModel) in.readObject();
            MapController mapController = (MapController) in.readObject();
            ItemController itemController = (ItemController) in.readObject();

            return new LoadedGameData(player, mapController, itemController);
        }
    }

    public List<String> getSavedFileNames() {
        List<String> savedGameFiles = new ArrayList<>();
        File[] listOfFiles = new File(SaveFilePathResolver.resolveSavesFolderPath()).listFiles();

        if (listOfFiles == null)
            return savedGameFiles;

        for (File file : listOfFiles) {
            if (file.getName().contains(".sav"))
                savedGameFiles.add(file.getName());
        }

        return savedGameFiles;
    }

    public boolean playerNameExistsAsASaveFile(String playerName) {
        return getSavedFileNames().stream()
                .anyMatch(fileName -> fileName.equalsIgnoreCase(playerName + ".sav"));
    }

    public String deleteSavedGame(String selectedFile) {
        File file = new File(SaveFilePathResolver.resolveSaveFilePath(selectedFile));

        return file.delete()
                ? SaveLoadMessagesEnum.DELETE_SUCCESS.getMessage()
                : SaveLoadMessagesEnum.DELETE_FAILURE.getMessage();
    }
}