package characters;

import java.util.ArrayList;
import java.util.List;

import characters.model.PlayerModel;
import map.model.DockYardModel;
import org.json.simple.JSONObject;

/**
 *
 * @author Vasilis Triantaris
 */
public class CaptainActionModel {

    List<DockYardModel> listOfDocks;

    public CaptainActionModel(List<DockYardModel> docks){
        this.listOfDocks = docks;
    }
 
    /**
     * Method that checks whether there is a person to sail on a different location.
     * If there is then a apropriate message is created which shows the destionation
     * and the fee required. If it doesnt exist then also a message is created.
     * 
     * @param player The player object.
     * @return Returns the message created.
     */
    public JSONObject TalkToCaptainProcess(PlayerModel player){

        String message = "There is no dockyard in this place!";
        boolean status = false;
        
        List<DockYardModel> listOfDocksThatAreConnectedToArea = new ArrayList();

        for(DockYardModel eachDock : this.listOfDocks){
            if(eachDock.getStartingDockLocation().getAreaName().equals(player.getLocation().getAreaName())){
                if(eachDock.getSailingFee() != 0.0)
                    message = "I can get you to "+ eachDock.getDestinationDockLocation().getAreaName() +
                        " for "+ eachDock.getSailingFee() +" gold coins.";
                
                status = true;
                listOfDocksThatAreConnectedToArea.add(eachDock);
            }     
        }

        JSONObject jObj = new JSONObject();
        jObj.put("message", message);
        jObj.put("status", status);
        jObj.put("docklist", listOfDocksThatAreConnectedToArea);
        
        return jObj;
    }
    
    
}
