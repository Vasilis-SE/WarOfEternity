package Serialization;

import characters.model.PlayerModel;
import GameFileConfiguration.SaveFolderConfig;
import Items.ItemController;
import map.controller.MapController;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SaveGameData {
  
    private PlayerModel player;
    private MapController mc;    
    private ItemController ic;

    //Constructor
    public SaveGameData(PlayerModel playerObj, MapController mcObj, ItemController icObj){
        this.player = playerObj;
        this.mc = mcObj;    
        this.ic = icObj;
    }
    
    /**
     * Method that saves the data of the specific game.
     */
    public void SavePlayerData(){

        String usersHome = System.getProperty("user.home");
        
        String saveFilePath = "";
        try {
            saveFilePath = java.net.URLDecoder.decode(usersHome+"\\WarOfEternity\\Saves\\"+this.player.getName()+".sav", "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SaveFolderConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        saveFilePath = saveFilePath.replace("%20", " ");
        
        try{
            FileOutputStream fileOut = new FileOutputStream(saveFilePath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            
            out.writeObject(this.player);
            out.writeObject(this.mc);
            out.writeObject(this.ic);

            out.close();
            fileOut.close();
        }
        catch(IOException i){
        }
      
    }  
    
}
