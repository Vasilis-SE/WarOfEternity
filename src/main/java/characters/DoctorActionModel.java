package characters;

import characters.model.PlayerModel;

/**
 *
 * @author Vasilis Triantaris
 */
public class DoctorActionModel {
    
    public DoctorActionModel(){
    }
    
    /**
     * Method that handles the doctor - healing process.
     * 
     * @param player The player object.
     * @return Returns a message as a result of talk to healer/doctor action command.
     */
    public String TalkingToDoctorProcess(PlayerModel player){
        String message = "";
        
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
