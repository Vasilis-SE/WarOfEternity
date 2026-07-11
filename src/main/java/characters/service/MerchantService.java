package characters.service;

import Items.Item;
import characters.enums.MerchantMessagesEnum;
import characters.model.MerchantModel;
import characters.model.PlayerModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MerchantService {

    private final PlayerService playerService;

    public void addItemToMerchantGoods(MerchantModel merchant, Item item) {
        merchant.getMerchantGoods().add(item);
    }

    /**
     * Handles the buy process of a transaction.
     *
     * @param merchant      The merchant object being traded with.
     * @param player        The player object.
     * @param merchantGood  The name of the item that the player wants to buy.
     * @return Returns a string message that will be displayed to the user which describes the result of the action.
     */
    public String buyItem(MerchantModel merchant, PlayerModel player, String merchantGood) {
        Item itemToBuy = findMerchantGood(merchant, merchantGood);

        if (itemToBuy == null)
            return MerchantMessagesEnum.ITEM_NOT_FOUND.format(merchantGood);

        if (playerService.calculatingPlayerInventoryItemWeight(player) + itemToBuy.GetItemWeight() > 100.0)
            return MerchantMessagesEnum.WEIGHT_LIMIT_EXCEEDED.getMessage();

        if (player.getGold() < itemToBuy.GetItemValueInGold())
            return MerchantMessagesEnum.CANNOT_AFFORD_ITEM.getMessage();

        player.setGold(player.getGold() - itemToBuy.GetItemValueInGold());

        for (Item eachItemOnInventory : player.getInventory()) {
            if (eachItemOnInventory.GetItemName().equals(itemToBuy.GetItemName()) && itemToBuy.GetItemType() == 1) {
                eachItemOnInventory.SetItemValue(eachItemOnInventory.GetItemValue() + 8);
                return MerchantMessagesEnum.PURCHASE_THANK_YOU.getMessage();
            }
        }

        playerService.addItemToSelectedItemsByPlayer(player, itemToBuy);

        return MerchantMessagesEnum.PURCHASE_THANK_YOU.getMessage();
    }

    /**
     * Handles the sell process of an item transaction.
     *
     * @param merchant   The merchant object being traded with.
     * @param player     The player object.
     * @param playerGood The name of the item that the player wants to sell.
     * @return Returns a string message that will be displayed to the user which describes the result of the action.
     */
    public String sellItem(MerchantModel merchant, PlayerModel player, String playerGood) {
        String message = "";

        for (Item eachSelectedItem : player.getInventory()) {
            if (eachSelectedItem.GetItemName().equalsIgnoreCase(playerGood)) {
                //The sell transaction type depends on the type of items to be sold, if an
                //item is a key it cannot be sold.
                switch (eachSelectedItem.GetItemType()) {
                    case 2:
                        message = MerchantMessagesEnum.ITEM_NOT_SELLABLE.getMessage();
                        break;

                    default:
                        playerService.removeItemFromPlayerEquippedInventory(player, eachSelectedItem);
                        playerService.addGoldToPlayer(player, eachSelectedItem.GetItemValueInGold());
                        playerService.removeItemFromSelectedItemsByPlayer(player, eachSelectedItem);
                        playerService.calculatePlayersArmor(player);
                        playerService.calculateGeneralPlayerDamage(player);
                        message = MerchantMessagesEnum.SALE_ASSISTANCE_OFFER.getMessage();
                        break;
                }
                break;
            } else {
                message = MerchantMessagesEnum.ITEM_NOT_OWNED.getMessage();
            }
        }

        return message;
    }

    private Item findMerchantGood(MerchantModel merchant, String merchantGood) {
        for (Item merchGood : merchant.getMerchantGoods())
            if (merchGood.GetItemName().equalsIgnoreCase(merchantGood.trim()))
                return merchGood;

        return null;
    }
}
