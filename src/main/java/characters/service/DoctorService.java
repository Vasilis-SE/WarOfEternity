package characters.service;

import characters.enums.DoctorMessagesEnum;
import player.model.PlayerModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DoctorService {


    /**
     * Method that handles the doctor - healing process.
     *
     * @param player The player object.
     * @return Returns a message as a result of talk to healer/doctor action command.
     */
    public String talkToDoctorProcess(PlayerModel player){
        if(player.getGold() < 10)
            return DoctorMessagesEnum.CANNOT_AFFORD_HEALING.getMessage();

        if(player.getHealth() == 100)
            return DoctorMessagesEnum.FULL_HEALTH.getMessage();

        int healthToBeRestored = 100 - player.getHealth();
        player.setHealth(player.getHealth() + healthToBeRestored);
        player.setGold(player.getGold() - 10.0);

        return DoctorMessagesEnum.HEALING_SUCCESS.format(healthToBeRestored);
    }
}
