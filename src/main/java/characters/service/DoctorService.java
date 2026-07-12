package characters.service;

import characters.model.PlayerModel;
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
            return "Iam sorry sir, you dont have enough money for me to heal you...";
        
        if(player.getHealth() == 100)
            return "You don't have a single scratch!";
        
        int healthToBeRestored = 100 - player.getHealth(); 
        player.setHealth(player.getHealth() + healthToBeRestored);
        player.setGold(player.getGold() - 10.0);
        
        return "The doctor healed " + healthToBeRestored + " health points, for 10 gold coins!";
    }
}
