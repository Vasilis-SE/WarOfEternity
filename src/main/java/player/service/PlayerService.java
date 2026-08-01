package player.service;

import characters.model.EnemyModel;
import item.model.ItemModel;
import map.model.AreaModel;
import player.enums.PlayerClassesEnum;
import player.interfaces.PlayerInterface;
import player.model.PlayerAttributeStatsModel;
import player.model.PlayerModel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PlayerService {

    private final PlayerFactoryService playerClassServiceFactory;

    public PlayerModel createNewPlayer(PlayerClassesEnum playerClass, String name, AreaModel startingAreaModel) {
        PlayerModel player = playerClassServiceFactory.getPlayer(playerClass).initPlayer(name, startingAreaModel);

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
     * players level the 10%. The exact attribute used depends on the player's class.
     */
    public void calculateGeneralPlayerDamage(PlayerModel player){
        int equippedItemDam = this.calculatePlayersDamageFromEquippedItems(player);
        PlayerInterface classService = playerClassServiceFactory.getPlayer(player.getPlayerClass());

        player.setDamage(classService.calculateDamage(player, equippedItemDam));
    }


    /**
     * Method that calculates the attribute points (strength, agility, intelligence)
     * of the player.
     */
    public void calculatePlayersAttributePoints(PlayerModel player){
        PlayerInterface classService = playerClassServiceFactory.getPlayer(player.getPlayerClass());
        PlayerAttributeStatsModel startingStats = classService.getStartingAttributeStats();
        PlayerAttributeStatsModel itemAttributeBonuses = getAttributePointsFromEquippedItems(player);

        player.setStrength(itemAttributeBonuses.getStrength() + startingStats.getStrength());
        player.setAgility(itemAttributeBonuses.getAgility() + startingStats.getAgility());
        player.setIntelligence(itemAttributeBonuses.getIntelligence() + startingStats.getIntelligence());
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


    /**
     * Method that checks whether the player's health has dropped to zero or
     * below, meaning the player has died.
     *
     * @param player The object that holds all the player data.
     * @return Returns true if the player is dead.
     */
    public boolean isPlayerDead(PlayerModel player){
        return player.getHealth() <= 0;
    }

    /**
     * Method that checks whether the player has reached the area that
     * concludes the game.
     *
     * @param player The object that holds all the player data.
     * @return Returns true if the player is standing on the final area.
     */
    public boolean hasPlayerReachedFinalArea(PlayerModel player){
        return player.getLocation().getAreaName().equals("Jade Sea Depths");
    }

    private PlayerAttributeStatsModel getAttributePointsFromEquippedItems(PlayerModel player){
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

        return new PlayerAttributeStatsModel(strFromItems, agiFromItems, intelFromItems);
    }

    /**
     * Method that increase the level of the player by one and also increases
     * the armor status and damage status by 2 points.
     */
    private void levelUp(PlayerModel player){
        player.setLevel(player.getLevel() + 1);
        player.setArmor(player.getArmor() + 2);

        PlayerInterface classService = playerClassServiceFactory.getPlayer(player.getPlayerClass());
        PlayerAttributeStatsModel startingStats = classService.getStartingAttributeStats();
        PlayerAttributeStatsModel itemAttributeBonuses = getAttributePointsFromEquippedItems(player);

        classService.applyLevelUpAttributeGrowth(player, startingStats, itemAttributeBonuses);

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

}
