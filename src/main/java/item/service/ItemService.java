package item.service;

import utils.TextFileProcessing;
import characters.controller.BattleController;
import characters.model.EnemyModel;
import characters.model.PlayerModel;
import characters.service.BattleService;
import characters.service.PlayerService;
import item.model.ItemConnectionModel;
import item.model.ItemModel;
import lombok.RequiredArgsConstructor;
import map.model.AreaModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that handles the reading of item data from the game data files,
 * the item action commands (equip, use, search, pick) and the stone tablet
 * inspection lookups.
 *
 * @author Vasilis Triantaris
 */
@RequiredArgsConstructor
public class ItemService {

    private final PlayerService playerService;
    private final BattleService battleService;

    // ================= Item data loading =================

    /**
     * Method that reads the GameItems json file and builds the list of game
     * items, for their specific item type. Each item type has its own
     * constructor on the ItemModel class that initializes the object.
     *
     * @param areaModels The list of game areas.
     * @return Returns the list of items read from the data file.
     */
    public List<ItemModel> setItemDataList(List<AreaModel> areaModels){
        String itemBuffer = TextFileProcessing.readResource("/DataAccessObjects/GameItems.json");
        List<ItemModel> listOfGameItems = new ArrayList<>();

        try{
            JSONArray itemEntries = (JSONArray) new JSONParser().parse(itemBuffer);

            for(Object entry : itemEntries)
                listOfGameItems.add(buildItemFromEntry((JSONObject) entry, areaModels));
        }
        catch(ParseException ex){
        }

        return listOfGameItems;
    }

    /**
     * Method that builds an item object out of a single GameItems json entry.
     * Each item type only carries the fields relevant to it, mirroring the
     * item type specific constructors of ItemModel.
     *
     * @param itemEntry The json object of a single item entry.
     * @param areaModels The list of game areas.
     * @return Returns the built item.
     */
    private ItemModel buildItemFromEntry(JSONObject itemEntry, List<AreaModel> areaModels){
        String name = (String) itemEntry.get("name");
        String description = (String) itemEntry.get("description");
        int type = ((Number) itemEntry.get("type")).intValue();
        double weight = ((Number) itemEntry.get("weight")).doubleValue();

        switch(type){

            //Case the item is consumable
            case 1 :
                return new ItemModel(name, description, type, weight,
                        ((Number) itemEntry.get("value")).intValue(),
                        ((Number) itemEntry.get("cost")).doubleValue(),
                        ((Number) itemEntry.get("healingPower")).intValue());

            //Case the item is miscenelous
            case 2 :
                return new ItemModel(name, description, type, weight,
                        ((Number) itemEntry.get("value")).intValue(),
                        ((Number) itemEntry.get("cost")).doubleValue());

            //Case the item is a weapon
            case 3 :
                return new ItemModel(name, description, type, weight,
                        ((Number) itemEntry.get("value")).intValue(), (String) itemEntry.get("attributeType"),
                        ((Number) itemEntry.get("attributeValue")).intValue(), getEligibleAreaForItem((String) itemEntry.get("area"), areaModels),
                        ((Number) itemEntry.get("cost")).doubleValue());

            //Case the item is a gate
            case 4 :
                return new ItemModel(name, description, type, weight,
                        ((Number) itemEntry.get("value")).intValue(),
                        ((Number) itemEntry.get("cost")).doubleValue(), (String) itemEntry.get("blockingDirection"));

            //Case the item is armor / shield
            case 5 :
            case 6 :
                return new ItemModel(name, description, type, weight,
                        ((Number) itemEntry.get("value")).intValue(), getEligibleAreaForItem((String) itemEntry.get("area"), areaModels),
                        ((Number) itemEntry.get("cost")).doubleValue());

            //Case the item is stone tablet.
            case 7 :
                return new ItemModel(name, description, type, weight,
                        getEligibleAreaForItem((String) itemEntry.get("area"), areaModels));

            default :
                return null;
        }
    }

    /**
     * Method that reads the GameItemConnections json file and sets on each
     * item its eligible connection with the specific area.
     *
     * @param listOfGameItems The list of game items already read from the data file.
     * @param areaModels The list of game areas.
     */
    public void setItemConnectionMainMethod(List<ItemModel> listOfGameItems, List<AreaModel> areaModels){

        String itemConnectionsBuffer = TextFileProcessing.readResource("/DataAccessObjects/GameItemConnections.json");

        try{
            JSONArray connectionEntries = (JSONArray) new JSONParser().parse(itemConnectionsBuffer);

            for(Object entry : connectionEntries){
                JSONObject connectionEntry = (JSONObject) entry;

                ItemModel itemRef = getEligibleGameItem((String) connectionEntry.get("item"), listOfGameItems);
                AreaModel areaModelConnection = getEligibleAreaForItem((String) connectionEntry.get("area"), areaModels);
                String itemPurpose = (String) connectionEntry.get("usage");

                setItemConnectionWithAreas(itemRef, areaModelConnection, itemPurpose, listOfGameItems);
            }
        }
        catch(ParseException ex){
        }
    }

    /**
     * Method that finds the eligible game area that the item can be found in.
     *
     * @param itemAreaName The name of the area in string form.
     * @param areaModels The list of game areas.
     * @return Returns an area object that is the eligible item area.
     */
    private AreaModel getEligibleAreaForItem(String itemAreaName, List<AreaModel> areaModels){

        AreaModel eligibleItemAreaModel = null;

        for(AreaModel eachGameAreaModel : areaModels){
            if(eachGameAreaModel.getAreaName().equals(itemAreaName))
                eligibleItemAreaModel = eachGameAreaModel;
        }

        return eligibleItemAreaModel;
    }

    /**
     * Method that finds the eligible game item that will be used on the item
     * connections.
     *
     * @param itemName The name of the item.
     * @param listOfGameItems The list of game items.
     * @return Returns the item that is eligible on the specific item connection.
     */
    private ItemModel getEligibleGameItem(String itemName, List<ItemModel> listOfGameItems){

        ItemModel eligibleItem = null;

        for(ItemModel eachItem : listOfGameItems){
            if(eachItem.getItemName().equals(itemName))
                eligibleItem = eachItem;
        }

        return eligibleItem;
    }

    private void setItemConnectionWithAreas(ItemModel itemRef, AreaModel areaModelConnection, String itemPurpose, List<ItemModel> listOfGameItems){

        ItemConnectionModel icwa = new ItemConnectionModel(areaModelConnection, itemRef, itemPurpose);

        for(ItemModel eachItem : listOfGameItems){
            if(eachItem.getItemName().equalsIgnoreCase(itemRef.getItemName())){
                eachItem.addItemConnectionWithAreaToList(icwa);
            }
        }
    }

    /**
     * Method that restocks consumable items (potions) that have run out, so
     * they can be found again on their area.
     *
     * @param listOfItems The list of game items.
     */
    public void restockDepletedConsumables(List<ItemModel> listOfItems){
        for(ItemModel eachItem : listOfItems){
            if(eachItem.getItemType() == 1 && eachItem.getItemValue() == 0)
                eachItem.setItemValue(8);
        }
    }

    // ================= Item action commands =================

    /**
     * This method finds the specific item to be used by the player and returns
     * its item type.
     *
     * @param player The player object.
     * @param nounPartOfCommand The noun part of the command (the item name).
     * @return Returns the integer code of the item type.
     */
    public int getTypeOfItemForUsagePurpose(PlayerModel player, String nounPartOfCommand){
        int itemType = 0;

        for(ItemModel eachSelectedItem : player.getInventory()){
            if(eachSelectedItem.getItemName().equalsIgnoreCase(nounPartOfCommand))
                itemType = eachSelectedItem.getItemType();
        }

        return itemType;
    }

    /**
     * Method that handles the equip item action of the player.
     *
     * @param player The object that holds all the player data.
     * @param equipItemCommand The name of the item to be equipped.
     * @return Returns a message from equipping an item action command.
     */
    public String equipItemPlayerAction(PlayerModel player, String equipItemCommand){
        String message;
        ItemModel itemToBeEquiped = null;

        for(ItemModel eachItemOnInventory : player.getInventory()){
            //If the item to be equiped existes in the inventory
            if(eachItemOnInventory.getItemName().equalsIgnoreCase(equipItemCommand))
                itemToBeEquiped = eachItemOnInventory;
        }

        if(itemToBeEquiped != null){
            switch(itemToBeEquiped.getItemType()){
                case 1 :
                case 2 :
                    message = "This item cant be equiped!";
                break;

                default :
                    removeItemWithSameTypeThatIsAlreadyEquippedOnPlayer(player, itemToBeEquiped);
                    playerService.addItemToEquippedItemListOfPlayer(player, itemToBeEquiped);
                    playerService.calculatePlayersAttributePoints(player);
                    playerService.calculateGeneralPlayerDamage(player);
                    playerService.calculatePlayersArmor(player);
                    message = itemToBeEquiped.getItemName()+" is equiped!";
                break;
            }
        }
        else{
            message = "There no such item in your inventory!";
        }

        return message;
    }

    /**
     * Method that if there is an item with the same type already equipped then
     * it removes it from the list of equipped items.
     *
     * @param player The object that holds all the player data.
     * @param itemToBeEquiped The specific item that is going to replace an existing of its same item type when going to equip.
     */
    private void removeItemWithSameTypeThatIsAlreadyEquippedOnPlayer(PlayerModel player, ItemModel itemToBeEquiped){
        List<ItemModel> newEquipedItemList = new ArrayList<>();

        for(ItemModel eachEquipedItem : player.getEquippedItems())
            if(eachEquipedItem.getItemType() != itemToBeEquiped.getItemType())
                newEquipedItemList.add(eachEquipedItem);

        player.setEquippedItems(newEquipedItemList);
    }

    /**
     * Method that processes the use of potion items command actions.
     *
     * @param player The object that holds all the player data.
     * @param enemyController The object of the enemy controller class. It is used to decide whether the player is in a battle phase.
     * @param enemyToCombat The enemy object that the user is battling. It is used in order to maintain the battle cycle.
     * @param nounPartOfCommand The noun part of the command (the item name).
     * @return Returns a message that describes the result of a use potion item action command. This message will be shown to the user.
     */
    public String playerUseItemPotionCommand(PlayerModel player, BattleController enemyController, EnemyModel enemyToCombat, String nounPartOfCommand){
        String message = "";
        List<ItemModel> newInventory = new ArrayList<>();

        int i=0;
        for(ItemModel eachItemOnInventory : player.getInventory()){
            if((eachItemOnInventory.getItemName().equalsIgnoreCase(nounPartOfCommand)) && (eachItemOnInventory.getItemValue() > 0)){
                player.setHealth(player.getHealth() + eachItemOnInventory.getItemHealingPower());
                player.getInventory().get(i).setItemValue(player.getInventory().get(i).getItemValue() - 1);

                //If the player runs out of potion then the item must be removed from inventory
                if(player.getInventory().get(i).getItemValue() == 0){
                    for(ItemModel eachItem: player.getInventory()){
                        if(!eachItem.getItemName().equals(eachItemOnInventory.getItemName()))
                            newInventory.add(eachItem);
                    }

                    player.setInventory(newInventory);
                }

                message = eachItemOnInventory.getItemName()+" has been used!";
            }
            i++;
        }

        if(enemyController.getBattleState())
            message += "\n" + battleService.attackFromEnemyToPlayerProcess(enemyToCombat, player);

        return message;
    }

    /**
     * Method that processes the use of keys command actions.
     *
     * @param player The object that holds all the player data.
     * @param listOfItems The list of game items.
     * @param nounPartOfCommand The noun part of the command (the item name).
     * @return Returns a message that describes the result of a use item action command.
     */
    public String playerUseItemKeyActionCommand(PlayerModel player, List<ItemModel> listOfItems, String nounPartOfCommand){
        boolean itemCanBeUsedOnSpecificArea = false;
        String doorItemNameOnArea;
        String message = "";

        int i = 0;
        for(ItemModel eachItem : listOfItems){
            for(ItemConnectionModel icwa : eachItem.getItemConnectionsWithArea()) {

                //If the item item to be used its purposed for usage and its already picked by the player and there
                //is a usage connection of the item with the speciific are that the player is on then ...
                if((icwa.getItemUsage().equals("use")) && (icwa.getConnectionWithAreaReference().getAreaName().equals(player.getLocation().getAreaName()))){
                    if(listOfItems.get(i).getItemValue() != 0){

                        switch(listOfItems.get(i).getItemType()){

                            //case item is misceleneous
                            case 2 :
                                doorItemNameOnArea = getGateNameFromItemList(player, listOfItems);
                                String gateIntegrityMessage = usageIntegrityOfTheGate(player, listOfItems, nounPartOfCommand);
                                if(!gateIntegrityMessage.isEmpty())
                                    return gateIntegrityMessage;

                                changeStateOfGate(doorItemNameOnArea, listOfItems);
                                itemCanBeUsedOnSpecificArea = true;
                                message = "Gate has been open!";
                            break;
                       }
                    }
                    else{
                        message = "Item : " + nounPartOfCommand + " has already been used!";
                    }
                }
            }
            i++;
        }

        if(!itemCanBeUsedOnSpecificArea)
            return "This item can't be used here!";

        return message;
    }

    private String getGateNameFromItemList(PlayerModel player, List<ItemModel> listOfItems){
        String doorItemNameOnArea = "";

        for(ItemModel eachItem : listOfItems){
            for(ItemConnectionModel icwa : eachItem.getItemConnectionsWithArea()){

                //If the item connection area is the same one with the players and its a door / gate
                if((icwa.getConnectionWithAreaReference().getAreaName().equals(player.getLocation().getAreaName())) && (eachItem.getItemType() == 4))
                    doorItemNameOnArea = eachItem.getItemName();
            }
        }

        return doorItemNameOnArea;
    }

    private String usageIntegrityOfTheGate(PlayerModel player, List<ItemModel> listOfItems, String nounPartOfCommand){
        String message = "";
        String gateLocation = "";
        String keyLocation = "";

        for(ItemModel eachItem : listOfItems){
            for(ItemConnectionModel icwa : eachItem.getItemConnectionsWithArea()){

                //If the item connection area is the same one with the players and its a door / gate
                if((icwa.getConnectionWithAreaReference().getAreaName().equals(player.getLocation().getAreaName())) && (eachItem.getItemType() == 4))
                    gateLocation = icwa.getConnectionWithAreaReference().getAreaName();

                if((icwa.getItemConnectedToAreaReference().getItemName().equalsIgnoreCase(nounPartOfCommand)) && (icwa.getItemUsage().equalsIgnoreCase("use")))
                    keyLocation = icwa.getConnectionWithAreaReference().getAreaName();
            }
        }

        if(!keyLocation.equals(gateLocation))
            message = "The item : " + nounPartOfCommand + " can't be used here!";

        return message;
    }

    private void changeStateOfGate(String doorName, List<ItemModel> listOfItems){
        int i = 0;
        for(ItemModel eachItem : listOfItems){
            if(eachItem.getItemName().equals(doorName))
                listOfItems.get(i).setItemValue(1);

            i++;
        }
    }

    /**
     * Method that handles the search for items action.
     *
     * @param player The player object.
     * @param listOfItems The list of game items.
     * @return Returns a message as a result of the search command and it is displayed to the user.
     */
    public String playerSearchItemProcess(PlayerModel player, List<ItemModel> listOfItems){
        String message = "";

        for(ItemModel eachItem : listOfItems){
            for(ItemConnectionModel icwa : eachItem.getItemConnectionsWithArea()){
                if((icwa.getItemUsage().equals("pick")) && (icwa.getConnectionWithAreaReference().getAreaName().equals(player.getLocation().getAreaName())) && (eachItem.getItemValue() == 0))
                    message += "--> "+eachItem.getItemDescription()+"\n";

                if((icwa.getItemUsage().equals("open")) && (icwa.getConnectionWithAreaReference().getAreaName().equals(player.getLocation().getAreaName())) && (eachItem.getItemValue() == 0))
                    message += "--> "+eachItem.getItemDescription()+"\n";
            }
        }

        ItemModel eligibleTablet = getEligibleItemFromArea(player, listOfItems);
        if(eligibleTablet != null)
                message += "--> A "+eligibleTablet.getItemName();

        if(message.isEmpty())
            message = "Nothing found while searching!";
        else
            message = "While searching you found : \n"+message;

        return message;
    }

    /**
     * Method that processes the pick / take item command action.
     *
     * @param player The object that holds all the player data.
     * @param listOfItems The list of game items.
     * @param nounPartOfCommand The noun part of the command (the item name).
     * @return Returns a message that describes the result of a pick / take action command. This message will be shown to the user.
     */
    public String playerItemActionCommand(PlayerModel player, List<ItemModel> listOfItems, String nounPartOfCommand){
        boolean itemExistsOnTheAreaCheck = false;
        String message = "";

        int i=0;
        for(ItemModel eachItem : listOfItems){
            for(ItemConnectionModel icwa : eachItem.getItemConnectionsWithArea()) {

                //If the the item is the same with the one that the player is trying to pick and there
                //is a connection of this specific item with the area that the player is on then ...
                if(((eachItem.getItemName().equalsIgnoreCase(nounPartOfCommand)) || (eachItem.getItemDescription().contains(nounPartOfCommand))) &&
                        (icwa.getItemUsage().equals("pick")) && (icwa.getConnectionWithAreaReference().getAreaName().equals(player.getLocation().getAreaName()))) {

                    //if the summary of weight plus the items is more than the limit then..
                    if(playerService.calculatingPlayerInventoryItemWeight(player) + eachItem.getItemWeight() > 100.0)
                        return "Exceeding weight limit, can't pick that up!";

                    switch(listOfItems.get(i).getItemType()){

                        //case it is misceleneous
                        case 2 :
                           if(listOfItems.get(i).getItemValue() == 0){
                               itemExistsOnTheAreaCheck = true;
                               message = "Item " + nounPartOfCommand + " is picked.\n--> Description : " + eachItem.getItemDescription();
                               listOfItems.get(i).setItemValue(1);
                               playerService.addItemToSelectedItemsByPlayer(player, listOfItems.get(i));
                           }
                           else {
                               itemExistsOnTheAreaCheck = true;
                               message = "You have already picked : " + nounPartOfCommand;
                           }
                        break;

                        case 1:
                            if(listOfItems.get(i).getItemValue() > 0){
                                itemExistsOnTheAreaCheck = true;
                                message = "Item " + nounPartOfCommand + " is picked\n--> Decription : " + eachItem.getItemDescription();
                                listOfItems.get(i).setItemValue(0);
                                playerService.addItemToSelectedItemsByPlayer(player, listOfItems.get(i));
                            }
                            else{
                                itemExistsOnTheAreaCheck = true;
                                message = "You have already picked : " + nounPartOfCommand;
                            }
                        break;
                    }
                }
            }
            i++;
        }

        if(!itemExistsOnTheAreaCheck)
            message = "There is no item : " + nounPartOfCommand;

        return message;
    }

    // ================= Stone tablet inspection =================

    /**
     * Method that handles the stone tablet inspection command.
     *
     * @param player The object of the player.
     * @param listOfItems The list of game items.
     * @return Returns a json object that contains the results of the process.
     */
    public JSONObject getStoneTabletOnAreaIfExists(PlayerModel player, List<ItemModel> listOfItems){

        JSONObject jObj = new JSONObject();
        boolean status = false;
        ItemModel stoneTablet = null;
        String message = "There is no stone tablet here to inspect !";

        ItemModel eligibleTablet = getEligibleItemFromArea(player, listOfItems);
        if(eligibleTablet != null){

            stoneTablet = eligibleTablet;
            status = true;
            message = "";
        }

        jObj.put("status", status);
        jObj.put("message", message);
        jObj.put("item", stoneTablet);

        return jObj;
    }

    /**
     * Method that finds all the stone tablet items from the list of items and
     * creates a list of them.
     *
     * @param listOfItems The list of game items.
     * @return Returns the list of stone tablets.
     */
    private List<ItemModel> getListOfStoneTablets(List<ItemModel> listOfItems){

        List<ItemModel> listOfStoneTablets = new ArrayList<>();

        for(ItemModel eachItem : listOfItems){
            if(eachItem.getItemType() == 7)
                listOfStoneTablets.add(eachItem);
        }

        return listOfStoneTablets;
    }

    /**
     * Method that finds the eligible stone tablet connected to the area that
     * the player is located.
     *
     * @param player The object of the player.
     * @param listOfItems The list of game items.
     * @return Returns the eligible stone tablet.
     */
    private ItemModel getEligibleItemFromArea(PlayerModel player, List<ItemModel> listOfItems){

        List<ItemModel> listOfStoneTablets = getListOfStoneTablets(listOfItems);
        ItemModel eligibleTablet = null;

        for(ItemModel eachTablet : listOfStoneTablets){

            if(eachTablet.getItemArea().getAreaName().equals(player.getLocation().getAreaName()))
                eligibleTablet = eachTablet;
        }

        return eligibleTablet;
    }

}