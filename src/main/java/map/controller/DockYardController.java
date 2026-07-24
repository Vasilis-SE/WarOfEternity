package map.controller;

import item.model.ItemModel;
import characters.model.PlayerModel;
import lombok.Getter;
import map.model.AreaModel;
import map.model.DockYardModel;
import map.service.DockYardActionService;
import map.service.DockYardConnectionService;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlling class for sail action commands.
 *
 * @author Vasilis Triantaris
 */
public class DockYardController {

    private final List<AreaModel> listOfAreaModels;
    @Getter
    private List<DockYardModel> listOfDockYards;
    private final List<ItemModel> listOfItems;

    public DockYardController(List<AreaModel> areaModels, List<ItemModel> items){
        this.listOfAreaModels = areaModels;
        this.listOfDockYards = new ArrayList<>();
        this.listOfItems = items;
    }

    /**
     * Controls the reading of the dockyard connections.
     */
    public void dockYardMainControllingMethod(){

        DockYardConnectionService dockYardConnectionService = new DockYardConnectionService();

        dockYardConnectionService.getTextFileColumnsToList();
        List<String> startStringDocks = dockYardConnectionService.getStartAreaStringList();
        List<String> destStringDocks = dockYardConnectionService.getDestinationStringList();

        List<AreaModel> startAreaModelDocks = dockYardConnectionService.getDockAreaList(startStringDocks, this.listOfAreaModels);
        List<AreaModel> destAreaModelDocks = dockYardConnectionService.getDockAreaList(destStringDocks, this.listOfAreaModels);

        dockYardConnectionService.setDockYardConnectionsToList(startAreaModelDocks, destAreaModelDocks);
        this.listOfDockYards = dockYardConnectionService.getDockYardList();
    }

    /**
     * Handles the command actions that have to do with sail.
     *
     * @param player The object that refers to the player.
     * @param docks The list of docks in the game.
     * @param noun The noun part of the command.
     * @param verb The verb part of the command.
     * @return Returns a string message that will be displayed to the user.
     */
    public String dockYardCommandActionProcess(PlayerModel player, List<DockYardModel> docks, String noun, String verb){
        String message;
        DockYardActionService dockYardActionService = new DockYardActionService(docks, noun, this.listOfItems);

        if(!verb.toLowerCase().equals("sink")){
            message = dockYardActionService.changeAreaOnSailAction(player);
        } else {
            message = dockYardActionService.sinkActionCommandProcess(player);
        }

        return message;
    }

}
