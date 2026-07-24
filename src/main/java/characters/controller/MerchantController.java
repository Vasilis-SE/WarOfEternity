package characters.controller;

import item.model.ItemModel;
import characters.model.MerchantModel;
import characters.model.PlayerModel;
import characters.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Class MerchantController is the controlling class for every merchant related
 * action command that occurs, delegating the business logic to MerchantService.
 *
 * @author Vasilis Triantaris
 */
@RequiredArgsConstructor
public class MerchantController {

    private final List<ItemModel> items;
    private final List<MerchantModel> merchants;
    private final MerchantService merchantService;

    public String talkToMerchantProcess(PlayerModel player) {
        return merchantService.talkToMerchantProcess(player, merchants, items);
    }

    public JSONObject canStartTransaction(String playersAreaName) {
        return merchantService.canStartTransaction(merchants, playersAreaName);
    }

    public String buyItem(PlayerModel player, String itemName) {
        JSONObject jObj = canStartTransaction(player.getLocation().getAreaName());

        if (!(boolean) jObj.get("status"))
            return (String) jObj.get("message");

        return merchantService.buyItem((MerchantModel) jObj.get("merchant"), player, itemName);
    }

    public String sellItem(PlayerModel player, String itemName) {
        JSONObject jObj = canStartTransaction(player.getLocation().getAreaName());

        if (!(boolean) jObj.get("status"))
            return (String) jObj.get("message");

        return merchantService.sellItem((MerchantModel) jObj.get("merchant"), player, itemName);
    }
}