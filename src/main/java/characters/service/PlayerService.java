package characters.service;

import item.model.ItemModel;
import map.model.AreaModel;
import characters.enums.PlayerClassesEnum;
import characters.model.EnemyModel;
import characters.model.PlayerModel;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PlayerService {


    public PlayerModel createNewPlayer(PlayerClassesEnum playerClass, String name, AreaModel startingAreaModel) {
        PlayerModel player = switch (playerClass) {
            case PlayerClassesEnum.WARRIOR -> PlayerModel.builder()
                    .playerClass(playerClass)
                    .strength(14)
                    .intelligence(6)
                    .agility(8)
                    .name(name)
                    .location(startingAreaModel)
                    .health(100)
                    .experience(0)
                    .inventory(new ArrayList<>())
                    .equippedItems(new ArrayList<>())
                    .build();
            case PlayerClassesEnum.ROGUE -> PlayerModel.builder()
                    .name(name)
                    .location(startingAreaModel)
                    .health(100)
                    .experience(0)
                    .playerClass(playerClass)
                    .strength(6)
                    .intelligence(8)
                    .agility(14)
                    .level(1)
                    .experience(0)
                    .inventory(new ArrayList<>())
                    .equippedItems(new ArrayList<>())
                    .build();
            case PlayerClassesEnum.MAGE -> PlayerModel.builder()
                    .name(name)
                    .location(startingAreaModel)
                    .health(100)
                    .experience(0)
                    .playerClass(playerClass)
                    .strength(5)
                    .intelligence(14)
                    .agility(9)
                    .level(1)
                    .experience(0)
                    .inventory(new ArrayList<>())
                    .equippedItems(new ArrayList<>())
                    .build();
        };

        calculateGeneralPlayerDamage(player);

        return player;
    }



    /**
     * Adding an item to the selected items of a player.
     *
     * @param player The player object that we want to add the item to.
     * @param item The item
     */
    public void addItemToSelectedItemsByPlayer(PlayerModel player, ItemModel item){
        player.getInventory().add(item);
    }


    /**
     * Adding an item to the equipped items of a player.
     *
     * @param player The player object that we want to add the item to.
     * @param item The item
     */
    public void addItemToEquippedItemListOfPlayer(PlayerModel player, ItemModel item){
        player.getEquippedItems().add(item);
    }


    /**
     * Calculating the whole wight of item in the inventory of player.
     *
     * @return Returns the whole weight of items that the player is carrying in a double data format.
     */
    public double calculatingPlayerInventoryItemWeight(PlayerModel player){
        double weightSum = 0.0;
        if(player.getInventory() == null) return weightSum;

        for(ItemModel eachItemInInventory : player.getInventory())
            weightSum += eachItemInInventory.getItemWeight();

        return weightSum;
    }


    /**
     * This method removes an item from the list of selected player items (players
     * inventory). Basically this method is used whenever an item is sold.
     *
     * @param item  The item to be removed from the inventory.
     */
    public void removeItemFromSelectedItemsByPlayer(PlayerModel player, ItemModel item){
        List<ItemModel> listOfNewSelectedItems = new ArrayList<>();

        if(player.getInventory() != null)
            for(ItemModel eachItem : player.getInventory())
                if(!eachItem.getItemName().equals(item.getItemName()))
                    listOfNewSelectedItems.add(eachItem);

        player.setInventory(listOfNewSelectedItems);
    }


    /**
     * This method is removes an item from the equipped item list of the player
     * whenever this item is about to be sold to the merchant.
     *
     * @param itemToBeSold  The item to be sold to the merchant
     */
    public void removeItemFromPlayerEquippedInventory(PlayerModel player, ItemModel itemToBeSold){
        List<ItemModel> newListOfEquippedItems = new ArrayList<>();

        if(player.getEquippedItems() != null)
            for(ItemModel eachEquipedItem : player.getEquippedItems())
                if(!eachEquipedItem.getItemName().equals(itemToBeSold.getItemName()))
                    newListOfEquippedItems.add(eachEquipedItem);

        player.setEquippedItems(newListOfEquippedItems);
    }



    /**
     * Calculates the whole armor value of the player from all the equipped
     * armor sets.
     */
    public void calculatePlayersArmor(PlayerModel player){
        int sum=0;

        for(ItemModel eachEquipedItem : player.getEquippedItems())
            if(eachEquipedItem.getItemType() == 5 || eachEquipedItem.getItemType() == 6)
                sum += eachEquipedItem.getItemValue();

        sum += 10;

        player.setArmor(sum);
    }


    public void addGoldToPlayer(PlayerModel player, double gold) {
        player.setGold(player.getGold() + gold);
    }

    public void subtractGoldToPlayer(PlayerModel player, double gold) {
        player.setGold(player.getGold() - gold);
    }


    /**
     * Method that calculates the final player damage. The attribute of the character
     * makes the 60% of the final damage while the item damage the 30% and the
     * players level the 10%.
     */
    public void calculateGeneralPlayerDamage(PlayerModel player){
        int equippedItemDam = this.calculatePlayersDamageFromEquippedItems(player);
        int damage;

        switch(player.getPlayerClass()){
            case PlayerClassesEnum.WARRIOR:
                damage = (int) ((equippedItemDam * 0.3) + (player.getStrength() * 0.6) + (player.getLevel() * 0.1));
                player.setDamage(damage);
                break;

            case PlayerClassesEnum.ROGUE:
                damage = (int) ((equippedItemDam * 0.3) + (player.getAgility() * 0.6) + (player.getLevel() * 0.1));
                player.setDamage(damage);
                break;

            case PlayerClassesEnum.MAGE:
                damage = (int) ((equippedItemDam * 0.3) + (player.getIntelligence() * 0.6) + (player.getLevel() * 0.1));
                player.setDamage(damage);
                break;
        }
    }


    /**
     * Method that calculates the attribute points (strength, agility, intelligence)
     * of the player.
     */
    public void calculatePlayersAttributePoints(PlayerModel player){
        int strengthAttribute = 0;
        int agilityAttribute = 0;
        int intelligenceAttribute = 0;

        JSONObject jObj = getPlayerClassStartingStats(player);

        for(ItemModel eachEquippedItem : player.getEquippedItems()){

            if(eachEquippedItem.getItemType() == 3 && eachEquippedItem.getAttributeType().equals("str"))
                strengthAttribute += eachEquippedItem.getAttributeValue();

            if(eachEquippedItem.getItemType() == 3 && eachEquippedItem.getAttributeType().equals("agi"))
                agilityAttribute += eachEquippedItem.getAttributeValue();

            if(eachEquippedItem.getItemType() == 3 && eachEquippedItem.getAttributeType().equals("int"))
                intelligenceAttribute += eachEquippedItem.getAttributeValue();

        }

        player.setStrength(strengthAttribute + (int) jObj.get("strength"));
        player.setAgility(agilityAttribute + (int) jObj.get("agility"));
        player.setIntelligence(intelligenceAttribute + (int) jObj.get("intelligence"));
    }


    /**
     * Method that handles the experience earned by a battle
     *
     * @param enemyThatBattled The enemy object that the user has battled.
     */
    public void battleExperienceEarned(PlayerModel player, EnemyModel enemyThatBattled){

        int expEarned = (int) (enemyThatBattled.getExperience() - (player.getLevel() * 0.3));

        if(expEarned < 0)
            expEarned = 1;

        //if the previous amount of experience plus the experience earned from
        //the enemy excedes the limit of 100 then the player levels up.
        if((player.getExperience() + expEarned) >= 100){
            levelUp(player);
            int newExperience = (player.getExperience() + expEarned) - 100;
            player.setExperience(newExperience);
        } else {
            int newExperience = player.getExperience() + expEarned;
            player.setExperience(newExperience);
        }
    }


    private JSONObject getAttributePointsFromEquippedItems(PlayerModel player){
        JSONObject jObj = new JSONObject();
        int strFromItems = 0;
        int agiFromItems = 0;
        int intelFromItems = 0;

        for(ItemModel eachEquippedItem : player.getEquippedItems()){
            if(eachEquippedItem.getItemType() == 3){
                switch(eachEquippedItem.getAttributeType()){

                    case "str":
                        strFromItems += eachEquippedItem.getAttributeValue();
                        break;

                    case "agi":
                        agiFromItems += eachEquippedItem.getAttributeValue();
                        break;

                    case "int":
                        intelFromItems += eachEquippedItem.getAttributeValue();
                        break;

                }
            }
        }

        jObj.put("itemstr", strFromItems);
        jObj.put("itemagi", agiFromItems);
        jObj.put("itemint", intelFromItems);

        return jObj;
    }

    /**
     * Method that increase the level of the player by one and also increases
     * the armor status and damage status by 2 points.
     */
    private void levelUp(PlayerModel player){
        player.setLevel(player.getLevel() + 1);
        player.setArmor(player.getArmor() + 2);

        JSONObject JSONBasicAttr = getPlayerClassStartingStats(player);
        JSONObject JSONItemAttr = getAttributePointsFromEquippedItems(player);

        switch(player.getPlayerClass()){

            case PlayerClassesEnum.WARRIOR:
                player.setStrength(((int) JSONBasicAttr.get("strength")) + ((int) JSONItemAttr.get("itemstr")) + ((player.getLevel() * 2) - 2));
                player.setAgility(((int) JSONBasicAttr.get("agility")) + ((int) JSONItemAttr.get("itemagi")) + (player.getLevel() - 1));
                player.setIntelligence(((int) JSONBasicAttr.get("intelligence")) + ((int) JSONItemAttr.get("itemint")) + (player.getLevel() - 1));
                break;

            case PlayerClassesEnum.ROGUE:
                player.setStrength(((int) JSONBasicAttr.get("strength")) + ((int) JSONItemAttr.get("itemstr")) + (player.getLevel() - 1));
                player.setAgility(((int) JSONBasicAttr.get("agility")) + ((int) JSONItemAttr.get("itemagi")) + ((player.getLevel() * 2) - 2));
                player.setIntelligence(((int) JSONBasicAttr.get("intelligence")) + ((int) JSONItemAttr.get("itemint")) + (player.getLevel() - 1));
                break;

            case PlayerClassesEnum.MAGE:
                player.setStrength(((int) JSONBasicAttr.get("strength")) + ((int) JSONItemAttr.get("itemstr")) + (player.getLevel() - 1));
                player.setAgility(((int) JSONBasicAttr.get("agility")) + ((int) JSONItemAttr.get("itemagi")) + (player.getLevel() - 1));
                player.setIntelligence(((int) JSONBasicAttr.get("intelligence")) + ((int) JSONItemAttr.get("itemint")) + ((player.getLevel() * 2) - 2));
                break;
        }

        this.calculateGeneralPlayerDamage(player);
    }






    /**
     * Calculates players damage from all the equipped weapon sets of the player.
     *
     * @return Returns the summary of the damage from the equipped items.
     */
    private int calculatePlayersDamageFromEquippedItems(PlayerModel player){
        int sum=10;

        for(ItemModel eachEquipedItem : player.getEquippedItems())
            if(eachEquipedItem.getItemType() == 3)
                sum += eachEquipedItem.getItemValue();

        return sum;
    }


    /**
     * Method that returns the starting attribute points for each class.
     * @return Returns a JSON object which contains the starting attribute points for each class.
     */
    private JSONObject getPlayerClassStartingStats(PlayerModel player){

        JSONObject jObj = new JSONObject();
        int strength = 0;
        int agility = 0;
        int intelligence = 0;

        switch(player.getPlayerClass()){

            case PlayerClassesEnum.WARRIOR:
                strength = 14;
                agility = 8;
                intelligence = 6;
                break;

            case PlayerClassesEnum.ROGUE:
                strength = 6;
                agility = 14;
                intelligence = 8;
                break;

            case PlayerClassesEnum.MAGE:
                strength = 5;
                agility = 9;
                intelligence = 14;
                break;
        }

        jObj.put("strength", strength);
        jObj.put("agility", agility);
        jObj.put("intelligence", intelligence);

        return jObj;
    }


}




