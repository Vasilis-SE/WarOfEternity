package characters.model;

import Items.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MerchantModel extends CharacterAbstractModel implements Serializable {

    private List<Item> merchantGoods;


    public void AddItemToMerchantGoods(Item item){
        this.merchantGoods.add(item);
    }

    /**
     * This method handles the buy process of a transaction.
     * 
     * @param player    The player object.
     * @param merchantGood  The object that the player wants to buy in string format.
     * @return Returns a string message that will be displayed to the user which describes the result of the action.
     */
    public String MerchantBuyItemProcess(PlayerModel player, String merchantGood){
        boolean itemExistance = false;
        Item itemToBuy = null;
        String message;
        
        //Finds the specific item that the user wants to buy
        for(Item merchGood : this.merchantGoods){
            if(merchGood.GetItemName().equalsIgnoreCase(merchantGood.trim())){
                itemToBuy = merchGood;
                itemExistance = true;
            }
        }
        
        if(itemExistance){

            if(player.CalculatingPlayerInventoryItemWeight() + itemToBuy.GetItemWeight() > 100.0)
                return "Exceeding weight limit, can't buy this item!";

            //checks if the player can aford to buy the specific item.
            if(player.getGold() >= itemToBuy.GetItemValueInGold()){
                
                player.setGold(player.getGold() - itemToBuy.GetItemValueInGold());

                for(Item eachItemOnInventory : player.getItemsSelected()){
                    if(eachItemOnInventory.GetItemName().equals(itemToBuy.GetItemName()) && itemToBuy.GetItemType() == 1){
                        eachItemOnInventory.SetItemValue(eachItemOnInventory.GetItemValue() + 8);
                        return "Thank you, can i do anything else for you sir ?";
                     }
                }
                
                player.AddItemToSelectedItemsByPlayer(itemToBuy);
                message = "Thank you, can i do anything else for you sir ?";
                
            }
            else{
                message = "Sorry sir, you cant afford this item...";
            }
        }
        else{
            message = "There is no item by the name "+merchantGood+" into my goods, sorry...";  
        }
        
        return message;
    }
    
    /**
     * This method handles the sell process of an item transaction.
     * 
     * @param player    The player object.
     * @param playerGood    The object that the user wants to sell in string format.
     * @return Returns a string message that will be displayed to the user which describes the result of the action.
     */
    public String MerchantSellItemProcess(PlayerModel player, String playerGood){
        String message = "";
 
        int i=0;
        for(Item eachSelectedItem : player.getItemsSelected()){
   
            if(eachSelectedItem.GetItemName().equalsIgnoreCase(playerGood)){
                //The sell transaction type depends on the type of items to be sold if an
                //items is a key it cannot be sold.
                switch(eachSelectedItem.GetItemType()){
                    case 2:
                        message = "I can't buy those, iam sorry...";
                    break;
                
                    default:
                        this.RemoveItemFromPlayerEquipedInventory(eachSelectedItem, player);
                        player.setGold(player.getGold() + eachSelectedItem.GetItemValueInGold());
                        player.RemoveItemFromSelectedItemsByPlayer(eachSelectedItem);
                        player.CalculatePlayersArmor();
                        player.CalculateGeneralPlayerDamage();
                        message = "Can i help you with anything else sir ?";
                    break;     
                }
                //If the 
                break;
            }
            else{
                message = "You can't sell me something that you don't have!";
            }
            i++;
        }
        
        return message;
    }
    
    /**
     * This method is removes an item from the equipped item list of the player
     * whenever this item is about to be sold to the merchant.
     * 
     * @param item  The item to be sold to the merchant and its already equipped.
     * @param player The player object.
     */
    public void RemoveItemFromPlayerEquipedInventory(Item item, PlayerModel player){
        List<Item> newListOfEquipedItems = new ArrayList<>();
        
        for(Item eachEquipedItem : player.getEquippedItems())
            if(!eachEquipedItem.GetItemName().equals(item.GetItemName()))
                newListOfEquipedItems.add(eachEquipedItem); 

        player.setEquippedItems(newListOfEquipedItems);
    }
    
}
