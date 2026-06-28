package characters.model;

import Items.Item;
import Items.ItemConnectionWithArea;
import characters.enums.PlayerClassesEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class PlayerModel extends CharacterAbstractModel implements Serializable {
    private PlayerClassesEnum playerClass;

    private int strength;
    private int intelligence;
    private int agility;

    private int level;
    private int experience;

    private List<Item> itemsSelected;
    private List<Item> equippedItems;

    public void AddItemToSelectedItemsByPlayer(Item item){
        this.itemsSelected.add(item);
    }
    
    public void AddItemToEquipedItemListOfPlayer(Item item){
        this.equippedItems.add(item);
    }

    
    /**
     * This method removes an item from the list of selected player items (players
     * inventory). Basically this method is used whenever an item is sold.
     * 
     * @param item  The item to be removed from the inventory.
     */
    public void RemoveItemFromSelectedItemsByPlayer(Item item){
        List<Item> listOfNewSelectedItems = new ArrayList<>();

        if(this.itemsSelected != null)
            for(Item eachItem : this.itemsSelected)
                if(!eachItem.GetItemName().equals(item.GetItemName()))
                    listOfNewSelectedItems.add(eachItem);

        this.itemsSelected = listOfNewSelectedItems;
    }
    
     /**
     * This method is removes an item from the equipped item list of the player
     * whenever this item is about to be sold to the merchant.
     * 
     * @param itemToBeSold  The item to be sold to the merchant
     */
    public void RemoveItemFromPlayerEquipedInventory(Item itemToBeSold){
        List<Item> newListOfEquippedItems = new ArrayList();

        if(this.equippedItems != null)
            for(Item eachEquipedItem : this.equippedItems)
                if(!eachEquipedItem.GetItemName().equals(itemToBeSold.GetItemName()))
                    newListOfEquippedItems.add(eachEquipedItem);

        this.equippedItems = newListOfEquippedItems;
    }
    
    
    
    /**
     * Calculates the whole armor value of the player from all the equipped
     * armor sets.
     */
    public void CalculatePlayersArmor(){
        int sum=0;
        
        for(Item eachEquipedItem : this.equippedItems)
            if(eachEquipedItem.GetItemType() == 5 || eachEquipedItem.GetItemType() == 6)
                sum += eachEquipedItem.GetItemValue();

        sum += 10;
        
        this.setArmor(sum);
    }
    
    /**
     * Calculates players damage from all the equipped weapon sets of the player.
     * 
     * @return Returns the summary of the damage from the equipped items.
     */
    public int CalculatePlayersDamageFromEquippedItems(){
        int sum=10;
        
        for(Item eachEquipedItem : this.equippedItems)
            if(eachEquipedItem.GetItemType() == 3)
                sum += eachEquipedItem.GetItemValue();

        return sum;
    }
    
    /**
     * Method that calculates the final player damage. The attribute of the character
     * makes the 60% of the final damage while the item damage the 30% and the
     * players level the 10%.
     */
    public void CalculateGeneralPlayerDamage(){
        int equippedItemDam = this.CalculatePlayersDamageFromEquippedItems();  
        int damage;
        
        switch(getPlayerClass()){
            
            case PlayerClassesEnum.WARRIOR:
                damage = (int) ((equippedItemDam * 0.3) + (this.strength * 0.6) + (this.level * 0.1));
                setDamage(damage);
            break;
                
            case PlayerClassesEnum.ROGUE:
                damage = (int) ((equippedItemDam * 0.3) + (this.agility * 0.6) + (this.level * 0.1));
                setDamage(damage);
            break;
                
            case PlayerClassesEnum.MAGE:
                damage = (int) ((equippedItemDam * 0.3) + (this.intelligence * 0.6) + (this.level * 0.1));
                setDamage(damage);
            break;
                
        }
        
    }
    
    /**
     * Method that calculates the attribute points (strength, agility, intelligence) 
     * of the player.
     */
    public void CalculatePlayersAttributePoints(){
        
        int strengthAttribute = 0;
        int agilityAttribute = 0;
        int intelligenceAttribute = 0;
        JSONObject jObj = this.GetPlayerClassStartingStats();
        
        for(Item eachEquippedItem : this.equippedItems){

            if(eachEquippedItem.GetItemType() == 3 && eachEquippedItem.GetAttributeType().equals("str"))
                strengthAttribute += eachEquippedItem.GetAttributeValue();

            if(eachEquippedItem.GetItemType() == 3 && eachEquippedItem.GetAttributeType().equals("agi"))
                agilityAttribute += eachEquippedItem.GetAttributeValue();
            
            if(eachEquippedItem.GetItemType() == 3 && eachEquippedItem.GetAttributeType().equals("int"))
                intelligenceAttribute += eachEquippedItem.GetAttributeValue();

        }
        
        setStrength(strengthAttribute + (int) jObj.get("strength"));
        setAgility(agilityAttribute + (int) jObj.get("agility"));
        setIntelligence(intelligenceAttribute + (int) jObj.get("intelligence"));
       
    }
    
    /**
     * Method that returns the starting attribute points for each class.
     * @return Returns a JSON object which contains the starting attribute points for each class.
     */
    private JSONObject GetPlayerClassStartingStats(){
        
        JSONObject jObj = new JSONObject();
        int strength = 0;
        int agility = 0;
        int intelligence = 0;
        
        switch(this.playerClass){
            
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
    
    /**
     * Calculating the whole wight of item in the inventory of player.
     * 
     * @return Returns the whole weight of items that the player is carrying in a double data format.
     */
    public double CalculatingPlayerInventoryItemWeight(){
        double weightSum = 0.0;

        if(this.itemsSelected != null)
            for(Item eachItemInInventory : this.itemsSelected)
                weightSum += eachItemInInventory.GetItemWeight();
 
        return weightSum;
    }
    
    /**
     * Method that returns the name of the gate in the specific area.
     * 
     * @param playerAction  The action command of the player.
     * @param itemList  The list of items of the game.
     * @return Returns the name of the door/gate that is blocking a specific path.
     */
    private String GetGateNameFromItemList(String playerAction, List<Item> itemList){
        String doorItemNameOnArea = "";
        
        for(Item eachItem : itemList){       
            for(ItemConnectionWithArea icwa : eachItem.GetItemConnectionsWithArea()){
                
                //If the item connection area is the same one with the players and its a door / gate 
                if((icwa.GetConnectionWithAreaReference().GetAreasName().equals(this.getLocation().GetAreasName())) && (eachItem.GetItemType() == 4))
                    doorItemNameOnArea = eachItem.GetItemName();
            }
        }
        
        return doorItemNameOnArea;
    }
    
    /**
     * Checks the integrity of the gate. That means if the gate is referred to open by  the key that
     * the player is trying to use.
     * 
     * @param playerAction  The action command of the player.
     * @param itemList  The list of items of the game.
     * @param doorItemNameOnArea The name of the gate the is blocking a specific path on the area.
     * @return Returns a message if the key that is gong to be used on a specific door/gate doesn't match.
     */
    private String UsageIntegrityOfTheGate(String playerAction, List<Item> itemList, String doorItemNameOnArea){
        String message = "";
        String gateLocation = "";
        String keyLocation = "";
        
        for(Item eachItem : itemList){       
            for(ItemConnectionWithArea icwa : eachItem.GetItemConnectionsWithArea()){
                
                //If the item connection area is the same one with the players and its a door / gate 
                if((icwa.GetConnectionWithAreaReference().GetAreasName().equals(this.getLocation().GetAreasName())) && (eachItem.GetItemType() == 4))
                    gateLocation = icwa.GetConnectionWithAreaReference().GetAreasName();

                if((icwa.GetItemConnectedToAreaReference().GetItemName().equalsIgnoreCase(playerAction)) && (icwa.GetItemUsage().equalsIgnoreCase("use")))
                    keyLocation = icwa.GetConnectionWithAreaReference().GetAreasName();
            }
        }

        if(!keyLocation.equals(gateLocation))
            message = "The item : "+playerAction+" can't be used here!";
            
        return message;
    }

    /**
     * Method that handles the experience earned by a battle
     *
     * @param enemyThatBattled The enemy object that the user has battled.
     */
    public void BattleExperienceEarned(EnemyModel enemyThatBattled){
        
        int expEarned = (int) (enemyThatBattled.getExperience() - (this.level * 0.3));
        
        if(expEarned < 0)
            expEarned = 1;
        
        //if the previous amount of experience plus the experience earned from
        //the enemy excedes the limit of 100 then the player levels up.
        if((this.experience + expEarned) >= 100){
            this.LevelUp();
            int newExperience = (this.experience + expEarned) - 100;
            this.setExperience(newExperience);
        } else {
            int newExperience = this.experience + expEarned;
            this.setExperience(newExperience);
        }
    }
   
    private JSONObject GetAttributePointsFromEquippedItems(){
        JSONObject jObj = new JSONObject();
        int strFromItems = 0;
        int agiFromItems = 0;
        int intelFromItems = 0;
        
        for(Item eachEquippedItem : this.equippedItems){
            if(eachEquippedItem.GetItemType() == 3){
                switch(eachEquippedItem.GetAttributeType()){
                    
                    case "str":
                        strFromItems += eachEquippedItem.GetAttributeValue();
                    break;
                    
                    case "agi":
                        agiFromItems += eachEquippedItem.GetAttributeValue();
                    break;
                    
                    case "int":
                        intelFromItems += eachEquippedItem.GetAttributeValue();
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
    private void LevelUp(){
        this.setLevel(this.level + 1);
        setArmor(getArmor() + 2);
        
        JSONObject JSONBasicAttr = this.GetPlayerClassStartingStats();
        JSONObject JSONItemAttr = GetAttributePointsFromEquippedItems();
        
        switch(this.playerClass){
            
            case PlayerClassesEnum.WARRIOR:
                setStrength(((int) JSONBasicAttr.get("strength")) + ((int) JSONItemAttr.get("itemstr")) + ((this.level * 2) - 2));
                setAgility(((int) JSONBasicAttr.get("agility")) + ((int) JSONItemAttr.get("itemagi")) + (this.level - 1));
                setIntelligence(((int) JSONBasicAttr.get("intelligence")) + ((int) JSONItemAttr.get("itemint")) + (this.level - 1));
            break;
                
            case PlayerClassesEnum.ROGUE:
                setStrength(((int) JSONBasicAttr.get("strength")) + ((int) JSONItemAttr.get("itemstr")) + (this.level - 1));
                setAgility(((int) JSONBasicAttr.get("agility")) + ((int) JSONItemAttr.get("itemagi")) + ((this.level * 2) - 2));
                setIntelligence(((int) JSONBasicAttr.get("intelligence")) + ((int) JSONItemAttr.get("itemint")) + (this.level - 1));
            break;
                
            case PlayerClassesEnum.MAGE:
                setStrength(((int) JSONBasicAttr.get("strength")) + ((int) JSONItemAttr.get("itemstr")) + (this.level - 1));
                setAgility(((int) JSONBasicAttr.get("agility")) + ((int) JSONItemAttr.get("itemagi")) + (this.level - 1));
                setIntelligence(((int) JSONBasicAttr.get("intelligence")) + ((int) JSONItemAttr.get("itemint")) + ((this.level * 2) - 2));
            break;
        }
        
        this.CalculateGeneralPlayerDamage();
    }
    
}
