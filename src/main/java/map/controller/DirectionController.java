package map.controller;

import characters.model.PlayerModel;
import item.model.ItemConnectionModel;
import item.model.ItemModel;
import lombok.RequiredArgsConstructor;
import map.model.AreaConnectionModel;

import java.util.List;

/**
 * Controlling class for direction action commands.
 *
 * @author Vasilis Triantaris
 */
@RequiredArgsConstructor
public class DirectionController {

    private final String nounPart;
    private final List<ItemModel> listOfGameItems;

    /**
     * Method manipulates the direction commands. It checks if the direction that
     * the player can go to exist or if it is accessible and if everything is fine
     * then it redirects him to the next area.
     *
     * @param player The object that holds all the player data.
     * @return Returns a message that describes the result of the player redirection action command given.
     */
    public String playerActionCommand(PlayerModel player){
        boolean directionToAreaIsCorrect = false;
        String directionIsBlockedMessage;
        String message = "";

        for(AreaConnectionModel acm : player.getLocation().getAreaConnections()){

            if(acm.getDirectionsOnCurrentArea().equalsIgnoreCase(this.nounPart)){

                directionIsBlockedMessage = directionToNextAreaIsBlockedByItem(player);

                if(directionIsBlockedMessage.isEmpty()){
                    player.setLocation(acm.getNextAreaModel());
                    directionToAreaIsCorrect = true;
                    message = player.getLocation().getAreaDescription();
                }
                else{
                    message = directionIsBlockedMessage;
                    directionToAreaIsCorrect = true;
                }
            }
        }

        if(!directionToAreaIsCorrect)
            message = "There is no direction to : "+this.nounPart;

        return message;
    }

    /**
     * This method checks whether the area that the user is in is blocked by a gate / door.
     *
     * @param player The object that holds all the player data.
     * @return Returns a message if the next area that the user is trying to go is blocked by a door/gate. Else it returns an empty message.
     */
    private String directionToNextAreaIsBlockedByItem(PlayerModel player){
        String checkMessage = "";

        for(ItemModel eachItem : this.listOfGameItems){
            for(ItemConnectionModel icwa : eachItem.getItemConnectionsWithArea()){

                //If the item in there is a object door/gate in the area that the user is in and is still
                //closed then...
                if((eachItem.getItemType() == 4) && (icwa.getItemUsage().equals("open") && (eachItem.getItemValue() == 0) && (eachItem.getBlockingDirection().equalsIgnoreCase(this.nounPart)) &&
                        (player.getLocation().getAreaName().equals(icwa.getConnectionWithAreaReference().getAreaName())))){
                    checkMessage = "You cannot proceed further. The gate is blocking your path!";
                }
            }
        }

        return checkMessage;
    }

}
