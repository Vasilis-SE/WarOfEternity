package characters.model;

import Items.Item;
import characters.service.PlayerService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@RequiredArgsConstructor
public class MerchantModel extends CharacterAbstractModel implements Serializable {

    private List<Item> merchantGoods;
    private final PlayerService playerService;

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

        if(!itemExistance)
            return "There is no item by the name "+merchantGood+" into my goods, sorry...";

        if(playerService.calculatingPlayerInventoryItemWeight(player) + itemToBuy.GetItemWeight() > 100.0)
            return "Exceeding weight limit, can't buy this item!";

        //checks if the player can aford to buy the specific item.
        if(player.getGold() < itemToBuy.GetItemValueInGold())
            return "Sorry sir, you cant afford this item...";

        player.setGold(player.getGold() - itemToBuy.GetItemValueInGold());

        for(Item eachItemOnInventory : player.getItemsSelected()){
            if(eachItemOnInventory.GetItemName().equals(itemToBuy.GetItemName()) && itemToBuy.GetItemType() == 1){
                eachItemOnInventory.SetItemValue(eachItemOnInventory.GetItemValue() + 8);
                return "Thank you, can i do anything else for you sir ?";
             }
        }

        playerService.addItemToSelectedItemsByPlayer(player, itemToBuy);
        message = "Thank you, can i do anything else for you sir ?";

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
                        this.removeItemFromPlayerEquippedInventory(eachSelectedItem, player);

                        playerService.addGoldToPlayer(player, eachSelectedItem.GetItemValueInGold());
                        playerService.removeItemFromSelectedItemsByPlayer(player, eachSelectedItem);
                        playerService.calculatePlayersArmor(player);
                        playerService.calculateGeneralPlayerDamage(player);
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
    public void removeItemFromPlayerEquippedInventory(Item item, PlayerModel player){
        List<Item> newListOfEquipedItems = new ArrayList<>();
        
        for(Item eachEquipedItem : player.getEquippedItems())
            if(!eachEquipedItem.GetItemName().equals(item.GetItemName()))
                newListOfEquipedItems.add(eachEquipedItem); 

        player.setEquippedItems(newListOfEquipedItems);
    }
    
}
