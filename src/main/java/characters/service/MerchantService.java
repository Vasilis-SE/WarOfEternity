package characters.service;

import GameFileConfiguration.TextFileProcessing;
import Items.Item;
import characters.enums.MerchantMessagesEnum;
import characters.model.MerchantModel;
import characters.model.PlayerModel;
import lombok.RequiredArgsConstructor;
import map.model.AreaModel;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MerchantService {

    private final PlayerService playerService;

    public void addItemToMerchantGoods(MerchantModel merchant, Item item) {
        merchant.getMerchantGoods().add(item);
    }

    /**
     * Method that reads the merchant connections data file and builds the
     * list of merchants placed on the given game areas.
     *
     * @param areaModels The list of game areas.
     * @return Returns the list of merchants read from the data file.
     */
    public List<MerchantModel> loadMerchants(List<AreaModel> areaModels) {
        StringBuffer merchantFileBuffer = TextFileProcessing.ReadResource("/DataAccessObjects/MerchantConnections.txt");

        List<String> merchantAreaNames = new ArrayList<>();
        List<String> merchantNames = new ArrayList<>();

        for (String eachLine : splitTextDataOnLines(merchantFileBuffer)) {
            String[] splitLineOnFields = eachLine.split("@");
            merchantAreaNames.add(splitLineOnFields[0].trim());
            merchantNames.add(splitLineOnFields[1].trim());
        }

        List<AreaModel> merchantAreaModels = getAreasAssociatedWithTheMerchants(merchantAreaNames, areaModels);

        return buildMerchantList(merchantNames, merchantAreaModels);
    }

    /**
     * Method that splits the string buffer into lines. Each line represents a
     * row into the table.
     *
     * @param merchantFileBuffer The buffer holding the merchant connections file content.
     * @return Returns a table of string in which every row is a line on the text file.
     */
    private String[] splitTextDataOnLines(StringBuffer merchantFileBuffer) {
        return merchantFileBuffer.toString().split("\n");
    }

    /**
     * Method that finds all the areas that merchants are connected to.
     *
     * @param merchantAreaNames The list of area names read from the data file.
     * @param areaModels             The list of game areas.
     * @return Returns the list of areas that merchants are located in.
     */
    private List<AreaModel> getAreasAssociatedWithTheMerchants(List<String> merchantAreaNames, List<AreaModel> areaModels) {
        List<AreaModel> listOfMerchantAreaModels = new ArrayList<>();

        for (String eachAreaString : merchantAreaNames)
            for (AreaModel eachGameAreaModel : areaModels)
                if (eachGameAreaModel.getAreaName().equals(eachAreaString))
                    listOfMerchantAreaModels.add(eachGameAreaModel);

        return listOfMerchantAreaModels;
    }

    /**
     * Method that sets the merchant data into a merchant list.
     *
     * @param merchantNames  The list of merchant names read from the data file.
     * @param merchantAreaModels  The list of areas that the merchants are located.
     * @return Returns the list of merchants.
     */
    private List<MerchantModel> buildMerchantList(List<String> merchantNames, List<AreaModel> merchantAreaModels) {
        List<MerchantModel> listOfMerchants = new ArrayList<>();
        for (int i = 0; i < merchantNames.size(); i++)
            listOfMerchants.add(MerchantModel.builder()
                    .location(merchantAreaModels.get(i))
                    .name(merchantNames.get(i))
                    .damage(0)
                    .merchantGoods(new ArrayList<>())
                    .build());

        return listOfMerchants;
    }

    /**
     * This method controls the reading of items for the merchant that will
     * be displayed to the player after a talk to merchant action command.
     *
     * @param player   The object that contains all the data for the player.
     * @param merchants The list of merchants available in the game.
     * @param items    The list of items available in the game.
     * @return Returns the inventory of the merchant analytically written object by object if everything goes wright or else a message that describes the result of the action command.
     */
    public String talkToMerchantProcess(PlayerModel player, List<MerchantModel> merchants, List<Item> items) {
        JSONObject jObj = canStartTransaction(merchants, player.getLocation().getAreaName());

        if (!(boolean) jObj.get("status"))
            return (String) jObj.get("message");

        MerchantModel merchant = (MerchantModel) jObj.get("merchant");
        if (merchant.getMerchantGoods() == null || merchant.getMerchantGoods().isEmpty())
            populateMerchantGoods(merchant, items);

        String resultMessage = "How can i help you sir?, I have got the best stuff for defence and offence. So what will it be?\n\n";
        resultMessage += buildMerchantInventoryMessage(merchant);

        return resultMessage;
    }

    /**
     * Method that checks if the location of the player is at the same of the merchants
     * so it can start a transaction.
     *
     * @param merchants       The list of merchants available in the game.
     * @param playersAreaName The name of the area the player is located in.
     * @return Returns a message if the user is trying to start a transaction in a area with no merchants. If there is a merchant then it returns an empty message.
     */
    public JSONObject canStartTransaction(List<MerchantModel> merchants, String playersAreaName) {
        JSONObject jObj = new JSONObject();
        String message = "There is no merchant in this area!";
        boolean status = false;
        MerchantModel merchant = null;

        for (MerchantModel eachGameMerchant : merchants) {
            if (eachGameMerchant.getLocation().getAreaName().equals(playersAreaName)) {
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
     * @param items    The list of items available in the game.
     */
    private void populateMerchantGoods(MerchantModel merchant, List<Item> items) {
        for (Item eachGameItem : items) {
            switch (eachGameItem.GetItemType()) {

                case 3:
                case 5:
                case 6:
                    if (eachGameItem.GetItemArea().getAreaName().equals(merchant.getLocation().getAreaName()))
                        addItemToMerchantGoods(merchant, eachGameItem);
                    break;

                case 1:
                    addItemToMerchantGoods(merchant, eachGameItem);
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
    private String buildMerchantInventoryMessage(MerchantModel merchant) {
        String inventory = "";

        for (Item merchGood : merchant.getMerchantGoods()) {

            //Depending on the type of item it processed the message
            switch (merchGood.GetItemType()) {
                case 1:
                    inventory += "Name : " + merchGood.GetItemName() + "\nDescription : " + merchGood.GetItemDescription() +
                            "\nHealing Power : " + merchGood.GetItemHealingPower() + "\nItem Quantity : 8" +
                            "\nItem Weight : " + merchGood.GetItemWeight() + " kg\nCost : " + merchGood.GetItemValueInGold() +
                            " gold\n---------------------------------------------\n";
                    break;

                case 3:
                    inventory += buildWeaponInventoryMessage(merchGood);
                    break;

                case 6:
                case 5:
                    inventory += "Name : " + merchGood.GetItemName() + "\nDescription : " + merchGood.GetItemDescription() +
                            "\nArmor : " + merchGood.GetItemValue() + "\nItem Weight : " + merchGood.GetItemWeight() +
                            " kg\nCost : " + merchGood.GetItemValueInGold() + " gold\n---------------------------------------------\n";
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
    private String buildWeaponInventoryMessage(Item merchGood) {
        String message = "";

        switch (merchGood.GetAttributeType()) {

            case "str":
                message = "Name : " + merchGood.GetItemName() + "\nDescription : " + merchGood.GetItemDescription() +
                        "\nStrength : " + merchGood.GetAttributeValue() +
                        "\nDamage : " + merchGood.GetItemValue() + "\nItem Weight : " + merchGood.GetItemWeight() +
                        " kg\nCost : " + merchGood.GetItemValueInGold() + " gold\n---------------------------------------------\n";
                break;

            case "agi":
                message = "Name : " + merchGood.GetItemName() + "\nDescription : " + merchGood.GetItemDescription() +
                        "\nAgility : " + merchGood.GetAttributeValue() +
                        "\nDamage : " + merchGood.GetItemValue() + "\nItem Weight : " + merchGood.GetItemWeight() +
                        " kg\nCost : " + merchGood.GetItemValueInGold() + " gold\n---------------------------------------------\n";
                break;

            case "int":
                message = "Name : " + merchGood.GetItemName() + "\nDescription : " + merchGood.GetItemDescription() +
                        "\nIntelligence : " + merchGood.GetAttributeValue() +
                        "\nDamage : " + merchGood.GetItemValue() + "\nItem Weight : " + merchGood.GetItemWeight() +
                        " kg\nCost : " + merchGood.GetItemValueInGold() + " gold\n---------------------------------------------\n";
                break;

        }

        return message;
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
            return MerchantMessagesEnum.ITEM_NOT_FOUND.getMessage();

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
