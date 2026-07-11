package map.service;

import Items.Item;
import Items.ItemConnectionWithArea;
import characters.CaptainActionModel;
import characters.model.PlayerModel;
import map.model.DockYardModel;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that handles all the sailing action such as sail to commands and
 * checking whether the destination is blocked.
 *
 * @author Vasilis Triantaris
 */
public class DockYardActionService {

    private final List<DockYardModel> listOfDocks;
    private final List<Item> listOfItems;
    private final String nounPart;

    public DockYardActionService(List<DockYardModel> docks, String noun, List<Item> items){
        this.listOfDocks = docks;
        this.listOfItems = items;
        this.nounPart = noun;
    }

    /**
     * Handles the sailing from one area to another. Firstly it checks whether
     * there is a captain on that particular area, then if the command given
     * contains the destination area and lastly if the player has the amount
     * of gold needed for the travel. At the end it returns a string message
     * which indicates the status of the transaction.
     *
     * @param player The object that contains the data of the player.
     * @return Returns a string message that indicates the status of the transaction.
     */
    public String changeAreaOnSailAction(PlayerModel player){
        String message;
        CaptainActionModel cam = new CaptainActionModel(this.listOfDocks);

        JSONObject jObj = cam.TalkToCaptainProcess(player);
        List<DockYardModel> docksOnArea = (List<DockYardModel>) jObj.get("docklist");

        if((boolean) jObj.get("status")){
            message = this.playerCanSailToHisDestination(player, docksOnArea);
        }
        else{
            message = "There is no dockyard in this place!";
        }

        return message;
    }

    /**
     * Checks if the desired sailing destination is reachable by the player. It
     * checks if the destination given exists on the current area, it checks if
     * the player has the gold to make the trip and if the destination is blocked.
     *
     * @param player The object that contains the data of the player.
     * @param docksOnArea The docks connected on the current player area.
     * @return Returns a message indicating the status of the process.
     */
    private String playerCanSailToHisDestination(PlayerModel player, List<DockYardModel> docksOnArea){

        String message;
        DockYardModel eligibleDockForArea = this.getEligibleDockForCurrentArea(docksOnArea);
        String noun = this.nounPart.replace("to", "");

        if((eligibleDockForArea != null) && (noun.trim().equalsIgnoreCase(eligibleDockForArea.getDestinationDockLocation().getAreaName()))){
            if(player.getGold() >= eligibleDockForArea.getSailingFee()){

                if(this.sailDestinationIsBlocked(player, eligibleDockForArea)){
                    message = "The way is unreachable!";
                }
                else{
                    player.setGold(player.getGold() - eligibleDockForArea.getSailingFee());
                    player.setLocation(eligibleDockForArea.getDestinationDockLocation());
                    message = eligibleDockForArea.getDestinationDockLocation().getAreaDescription();
                }
            }
            else{
                message = "You don't have enough gold coins to travel to "+eligibleDockForArea.getDestinationDockLocation().getAreaName();
            }
        }
        else{
            message = "There is no such destination!";
        }

        return message;
    }

    /**
     * Finds the eligible dock for the specific destination and the current player area.
     *
     * @param docksOnArea The docks connected on the current player area.
     * @return Returns the eligible dock yard.
     */
    private DockYardModel getEligibleDockForCurrentArea(List<DockYardModel> docksOnArea){

        DockYardModel eligibleDockForArea = null;
        for(DockYardModel eachDockOnArea : docksOnArea){
            if(this.nounPart.contains(eachDockOnArea.getDestinationDockLocation().getAreaName().toLowerCase())){
                eligibleDockForArea = eachDockOnArea;
            }
        }

        return eligibleDockForArea;
    }

    /**
     * Checks if the sailing destination is blocked.
     *
     * @param player The object that contains the data of the player.
     * @param eligibleDockForArea The eligible dock for the specific current area.
     * @return Returns a boolean variable that determines whether the destination is blocked.
     */
    private boolean sailDestinationIsBlocked(PlayerModel player, DockYardModel eligibleDockForArea){

        boolean check = false;
        List<Item> areaItems = this.getListOfItemsAssociatedWithTheArea(eligibleDockForArea);

        for(Item eachItem : areaItems){
            for(ItemConnectionWithArea eachConnection : eachItem.GetItemConnectionsWithArea()){

                if(eligibleDockForArea.getStartingDockLocation().getAreaName().equals(eachConnection.GetConnectionWithAreaReference().getAreaName())
                        && eachConnection.GetItemUsage().equals("open") && eachItem.GetItemValue() == 0
                        && this.nounPart.contains(eachItem.GetBlockingDirection().toLowerCase())){
                    check = true;
                }
            }
        }

        return check;
    }

    /**
     * Finds all the items associated with the area of the given dock yard.
     *
     * @param dockYard The eligible dock for the specific current area.
     * @return Returns the list of items connected to the area.
     */
    private List<Item> getListOfItemsAssociatedWithTheArea(DockYardModel dockYard){

        List<Item> itemsConnectedToArea = new ArrayList<>();

        for(Item eachItem : this.listOfItems){
            for(ItemConnectionWithArea eachConnection : eachItem.GetItemConnectionsWithArea()){

                if(eachConnection.GetConnectionWithAreaReference().getAreaName().equals(dockYard.getStartingDockLocation().getAreaName()))
                    itemsConnectedToArea.add(eachItem);
            }
        }

        return itemsConnectedToArea;
    }

    /**
     * Handles the sink ship action command by the user. Firstly it checks
     * whether the action is blocked by an item and then it implements the battle.
     *
     * @param player The object that contains the data of the player.
     * @return Returns a message indicating the status of the process.
     */
    public String sinkActionCommandProcess(PlayerModel player){

        String message;

        JSONObject jObj = this.findSinkArea(player);
        if(!(boolean) jObj.get("status"))
            return (String) jObj.get("message");

        DockYardModel eligibleSinkArea = (DockYardModel) jObj.get("areadock");
        player.setLocation(eligibleSinkArea.getDestinationDockLocation());
        message = eligibleSinkArea.getDestinationDockLocation().getAreaDescription();

        return message;
    }

    /**
     * Checks whether the area that the player is on can sink the ship and if the
     * area is blocked by any gates / doors. If the player can sink the ship then
     * returns the eligible sink area data.
     *
     * @param player The object that contains the data of the player.
     * @return Returns a JSON object that holds the message of the check, the status and the eligible sink area.
     */
    private JSONObject findSinkArea(PlayerModel player){

        JSONObject jObj = new JSONObject();
        DockYardModel sinkArea = null;
        String message = "You cannot sink the ship!";
        boolean status = false;

        for(DockYardModel eachDock : this.listOfDocks){
            for(Item eachItem : this.listOfItems){
                for(ItemConnectionWithArea icwa : eachItem.GetItemConnectionsWithArea()){

                    if((eachItem.GetItemType() == 4) && (icwa.GetItemUsage().equals("open")
                            && (eachItem.GetItemValue() == 0) && (eachItem.GetBlockingDirection().equalsIgnoreCase("sink")))
                            && player.getLocation().getAreaName().equals("The Great Jade Sea")){

                        message = "You cannot procced further, the beam is blocking the ship!";
                        status = false;
                    }
                    else if((eachItem.GetItemType() == 4) && (icwa.GetItemUsage().equals("open")
                            && (eachItem.GetItemValue() == 1) && (eachItem.GetBlockingDirection().equalsIgnoreCase("sink")))
                            && player.getLocation().getAreaName().equals("The Great Jade Sea")
                            && eachDock.getDestinationDockLocation().getAreaName().equals("Jade Sea Depths")){

                        message = "";
                        sinkArea = eachDock;
                        status = true;
                    }
                }
            }
        }

        jObj.put("message", message);
        jObj.put("status", status);
        jObj.put("areadock", sinkArea);

        return jObj;
    }

}
