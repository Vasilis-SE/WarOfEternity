package characters;

import Items.Item;
import Map.Area;
import java.util.List;

import characters.model.MerchantModel;
import characters.model.PlayerModel;
import org.json.simple.JSONObject;

/**
 *
 * @author Vasilis Triantaris
 */
public class MerchantActionModel {

    private final String verbPart;
    private final String nounPart;
    private final List<Item> listOfGameItems;
    private final List<Area> listOfGameAreas;
    private final List<MerchantModel> listOfGameMerchants;
    
    //Constructor
    public MerchantActionModel(String verb, String noun, List<Item> items, List<Area> areas, List<MerchantModel> merchants){
        this.verbPart = verb;
        this.nounPart = noun;
        this.listOfGameItems = items;
        this.listOfGameAreas = areas;
        this.listOfGameMerchants = merchants;
    }

    /**
     * This method controls the reading of items for the merchant that will
     * be displayed to the player after a talk to merchant action command.
     * 
     * @param player The object that contains all the data for the player.
     * @return Returns the inventory of the merchant analytically written object by object if everything goes wright or else a message that describes the result of the action command.
     */
    public String SetMerchantItemListToBeDisplayed(PlayerModel player){
   
        String resultMessage;
        JSONObject jObj = this.PlayersLocationCanStartATransaction(player.getLocation().GetAreasName());
        
        if(!(boolean) jObj.get("status"))
            return (String) jObj.get("message");
        
        
        MerchantModel merchant = (MerchantModel) jObj.get("merchant");
        if(merchant.getMerchantGoods().isEmpty())
            this.GetMerchandiseForTheMerchant((MerchantModel) jObj.get("merchant"));
        
        resultMessage = "How can i help you sir?, I have got the best stuff for defence and offence. So what will it be?\n\n";
        
        resultMessage += this.SetMerchantInventoryToBeDisplayed((MerchantModel) jObj.get("merchant"));
        
       
        return resultMessage;
    }
     
    /**
     * Method that checks if the location of the player is at the same of the merchants
     * so it can start a transaction.
     * 
     * @param playersAreaName The name of the player
     * @return Returns a message if the user is trying to start a transaction in a area with no merchants. If there is a merchant then it returns an empty message.
     */
    public JSONObject PlayersLocationCanStartATransaction(String playersAreaName){
        
        JSONObject jObj = new JSONObject();
        String message = "There is no merchant in this area!";
        boolean status = false;
        MerchantModel merchant = null;
        
        for(MerchantModel eachGameMerchant : this.listOfGameMerchants){
            if(eachGameMerchant.getLocation().GetAreasName().equals(playersAreaName)){
                message = "";
                status = true;
                merchant = eachGameMerchant;
            }
        }

        jObj.put("message", message);
        jObj.put("status", status);
        jObj.put("merchant", merchant);
        
        return jObj;
    }
    
    /**
     * Method that finds all the items that are referred for the specific merchant.
     * 
     * @param merchant The eligible merchant that is located in the same area as the player.
     */
    private void GetMerchandiseForTheMerchant(MerchantModel merchant){
        
        for(Item eachGameItem : this.listOfGameItems){
            switch(eachGameItem.GetItemType()){

                case 3:
                case 5:
                case 6:
                    if(eachGameItem.GetItemArea().GetAreasName().equals(merchant.getLocation().GetAreasName()))
                        merchant.AddItemToMerchantGoods(eachGameItem);
                break;
                    
                case 1:
                    merchant.AddItemToMerchantGoods(eachGameItem);
                break;
            }
        }
    
    }
    
    /**
     * Method that sets full description of each item that the merchant holds for
     * the items type.
     * 
     * @param merchant The eligible merchant that is located in the same area as the player.
     * @return Returns the full description of the merchants inventory.
     */
    private String SetMerchantInventoryToBeDisplayed(MerchantModel merchant){
        
        String inventory = "";

        for(Item merchGood : merchant.getMerchantGoods()){
        
            //Depending on the type of item it processed the message
            switch(merchGood.GetItemType()){
                case 1 :
                    inventory += "Name : "+merchGood.GetItemName()+"\nDescription : "+merchGood.GetItemDescription()+
                        "\nHealing Power : "+merchGood.GetItemHealingPower()+"\nItem Quantity : 8"+
                        "\nItem Weight : "+merchGood.GetItemWeight()+" kg\nCost : "+merchGood.GetItemValueInGold()+
                        " gold\n---------------------------------------------\n";   
                break;
                    
                case 3 :
                    inventory += this.SetWeaponInventoryMessage(merchGood);
                break;
                    
                case 6 :
                case 5 :
                    inventory += "Name : "+merchGood.GetItemName()+"\nDescription : "+merchGood.GetItemDescription()+
                        "\nArmor : "+merchGood.GetItemValue()+"\nItem Weight : "+merchGood.GetItemWeight()+
                        " kg\nCost : "+merchGood.GetItemValueInGold()+" gold\n---------------------------------------------\n";   
                break;  
            }
            
        }

        return inventory;
    }
    
    /**
     * Method that creates the merchant weapon item message for the specific 
     * attribute that the item gives.
     * 
     * @param merchGood The item that the merchant holds.
     * @return Returns the message of the weapon that will be displayed to the player.
     */
    private String SetWeaponInventoryMessage(Item merchGood){
    
        String message = "";
        
        switch(merchGood.GetAttributeType()){
        
            case "str":
               message = "Name : "+merchGood.GetItemName()+"\nDescription : "+merchGood.GetItemDescription()+
                        "\nStrength : "+merchGood.GetAttributeValue()+
                        "\nDamage : "+merchGood.GetItemValue()+"\nItem Weight : "+merchGood.GetItemWeight()+
                        " kg\nCost : "+merchGood.GetItemValueInGold()+" gold\n---------------------------------------------\n";   
            break;
                
            case "agi":
               message = "Name : "+merchGood.GetItemName()+"\nDescription : "+merchGood.GetItemDescription()+
                        "\nAgility : "+merchGood.GetAttributeValue()+
                        "\nDamage : "+merchGood.GetItemValue()+"\nItem Weight : "+merchGood.GetItemWeight()+
                        " kg\nCost : "+merchGood.GetItemValueInGold()+" gold\n---------------------------------------------\n";   
            break;
                
            case "int":
                message = "Name : "+merchGood.GetItemName()+"\nDescription : "+merchGood.GetItemDescription()+
                        "\nIntelligence : "+merchGood.GetAttributeValue()+
                        "\nDamage : "+merchGood.GetItemValue()+"\nItem Weight : "+merchGood.GetItemWeight()+
                        " kg\nCost : "+merchGood.GetItemValueInGold()+" gold\n---------------------------------------------\n";   
            break;
            
        }
        
        return message;
    }
    
    
}
