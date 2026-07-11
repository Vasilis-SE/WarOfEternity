package map.controller;

import Items.Item;
import characters.model.PlayerModel;
import lombok.Getter;
import map.model.Area;
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

    private final List<Area> listOfAreas;
    @Getter
    private List<DockYardModel> listOfDockYards;
    private final List<Item> listOfItems;

    public DockYardController(List<Area> areas, List<Item> items){
        this.listOfAreas = areas;
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

        List<Area> startAreaDocks = dockYardConnectionService.getDockAreaList(startStringDocks, this.listOfAreas);
        List<Area> destAreaDocks = dockYardConnectionService.getDockAreaList(destStringDocks, this.listOfAreas);

        dockYardConnectionService.setDockYardConnectionsToList(startAreaDocks, destAreaDocks);
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
