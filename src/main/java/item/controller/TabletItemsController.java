package item.controller;

import player.model.PlayerModel;
import characters.service.BattleService;
import player.service.PlayerFactoryService;
import player.service.PlayerService;
import item.enums.ItemMessagesEnum;
import item.model.ItemModel;
import item.service.ItemService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.json.simple.JSONObject;

/**
 * Class that handles the inspect action commands of the game.
 *
 * @author Vasilis Triantaris
 */
@RequiredArgsConstructor
public class TabletItemsController {

    private final String verbPart;
    private final String nounPart;
    private final List<ItemModel> listOfItems;

    /**
     * Main control method of the inspect commands.
     *
     * @param player The object of the player.
     * @return Returns a string that represents the result of the process.
     */
    public String tabletInspectActionCommand(PlayerModel player){

        String message;
        ItemService itemService = new ItemService(new PlayerService(new PlayerFactoryService()), new BattleService());

        JSONObject integrityJSON = itemService.getStoneTabletOnAreaIfExists(player, this.listOfItems);
        if(!(boolean) integrityJSON.get("status"))
            return (String) integrityJSON.get("message");

        ItemModel stoneTablet = (ItemModel) integrityJSON.get("item");
        message = ItemMessagesEnum.TABLET_DESCRIPTION.format(stoneTablet.getItemDescription());

        return message;
    }

}
