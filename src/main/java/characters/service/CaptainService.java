package characters.service;

import characters.enums.CaptainMessagesEnum;
import characters.model.PlayerModel;
import lombok.RequiredArgsConstructor;
import map.model.DockYardModel;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that handles the captain / dockyard talk action.
 *
 * @author Vasilis Triantaris
 */
@RequiredArgsConstructor
public class CaptainService {

    /**
     * Method that checks whether there is a person to sail on a different location.
     * If there is then a apropriate message is created which shows the destionation
     * and the fee required. If it doesnt exist then also a message is created.
     *
     * @param player The player object.
     * @param docks  The list of docks available in the game.
     * @return Returns the message created.
     */
    public JSONObject talkToCaptainProcess(PlayerModel player, List<DockYardModel> docks) {

        String message = CaptainMessagesEnum.NO_DOCKYARD_IN_AREA.getMessage();
        boolean status = false;

        List<DockYardModel> docksConnectedToArea = new ArrayList<>();

        for (DockYardModel eachDock : docks) {
            if (eachDock.getStartingDockLocation().getAreaName().equals(player.getLocation().getAreaName())) {
                if (eachDock.getSailingFee() != 0.0)
                    message = CaptainMessagesEnum.SAIL_OFFER.format(
                            eachDock.getDestinationDockLocation().getAreaName(), eachDock.getSailingFee());

                status = true;
                docksConnectedToArea.add(eachDock);
            }
        }

        JSONObject jObj = new JSONObject();
        jObj.put("message", message);
        jObj.put("status", status);
        jObj.put("docklist", docksConnectedToArea);

        return jObj;
    }
}