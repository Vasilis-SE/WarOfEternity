package item.controller;

import characters.controller.BattleController;
import characters.model.EnemyModel;
import characters.model.PlayerModel;
import characters.service.BattleService;
import characters.service.PlayerService;
import item.model.ItemModel;
import item.service.ItemService;
import lombok.Getter;
import map.model.AreaModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class Item Controller that controls the item objects and the
 * item connection objects.
 *
 * @author Vasilis Triantaris
 */
public class ItemController implements Serializable{

    private final List<AreaModel> listOfAreaModels;
    String verbPartOfCommand;
    String nounPartOfCommand;

    @Getter
    private List<ItemModel> listOfItems;

    //Constructor of item controller class
    public ItemController(List<AreaModel> areaModelList){

        this.listOfAreaModels = areaModelList;

        this.listOfItems = new ArrayList();
        this.verbPartOfCommand = "";
        this.nounPartOfCommand = "";
    }

    //Constructor for item action command.
    public ItemController(String verb, String noun, List<ItemModel> items){

        this.verbPartOfCommand = verb;
        this.nounPartOfCommand = noun;
        this.listOfItems = items;

        this.listOfAreaModels = new ArrayList();
    }

    /**
     * Method that controls the reading of all the item and item connections
     * files.
     */
    public void setItemDataForGame(){

        ItemService itemService = new ItemService(new PlayerService(), new BattleService());
        this.listOfItems = itemService.setItemDataList(this.listOfAreaModels);
        itemService.setItemConnectionMainMethod(this.listOfItems, this.listOfAreaModels);
    }

    public String itemActionCommandProcessController(PlayerModel player, BattleController enemyController, EnemyModel enemyToCombat){

        String resultMessage = null;
        ItemService itemService = new ItemService(new PlayerService(), new BattleService());

        if(this.verbPartOfCommand.equals("equip")){
            resultMessage = itemService.equipItemPlayerAction(player, this.nounPartOfCommand);
        }
        else if(this.verbPartOfCommand.equals("use")){
            switch(itemService.getTypeOfItemForUsagePurpose(player, this.nounPartOfCommand)){
                case 0 :
                    resultMessage = "There is no such item in your inventory!";
                break;

                case 1 :
                    resultMessage = itemService.playerUseItemPotionCommand(player, enemyController, enemyToCombat, this.nounPartOfCommand);
                break;

                case 2 :
                    resultMessage = itemService.playerUseItemKeyActionCommand(player, this.listOfItems, this.nounPartOfCommand);
                break;
            }
        }
        else if(this.verbPartOfCommand.equals("search")){
            resultMessage = itemService.playerSearchItemProcess(player, this.listOfItems);
        }
        else{
            resultMessage = itemService.playerItemActionCommand(player, this.listOfItems, this.nounPartOfCommand);
        }

        return resultMessage;
    }

}