package characters.controller;

import GameFileConfiguration.MusicConfiguration;
import Items.Item;
import Items.ItemController;
import Items.TabletItemsController;
import Map.Area;
import characters.DirectionActionModel;
import characters.DockYard;
import characters.enums.PlayerClassesEnum;
import characters.model.EnemyModel;
import characters.model.MerchantModel;
import characters.model.PlayerModel;
import characters.service.PlayerService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Class PlayeController is the controlling class for every player action command
 * that occurs. Through this class every command passes and with the help of the
 * verb parsers the application can easily determine which class to be called
 * from the specific command given by the player.
 *
 * @author Vasilis Triantaris
 */
@Getter
@Setter
@AllArgsConstructor
public class PlayerController {

    private EnemyModel enemyToBattle;
    private String playerCommandBeforeBattle;

    private PlayerService playerService;

    PlayerController() {
        enemyToBattle = new EnemyModel();
        playerCommandBeforeBattle = "";
        playerService = new PlayerService();
    }


    public String playerMainControllingMethodForActionDecision(PlayerModel player, List<Item> itemList,
                                                               EnemiesController enemyController, List<Area> areasList, List<DockYard> docksList,
                                                               List<MerchantModel> listOfMerchants, MusicConfiguration mcf,
                                                               String parsingDecision, String nounPartOfCommand, String verbPartOfCommand){
        
        String resultMessage = "";

        //Calls the method which checks the integrity of the battle (the type of commands to be used).
        JSONObject integrJSON = enemyController.battleIntegrityActionCheck(parsingDecision, verbPartOfCommand);
        if(!(boolean) integrJSON.get("status"))
            return (String) integrJSON.get("message");


        //Calls the method which triggers a battle on direction command.
        JSONObject battleTrigJSON = enemyController.triggerBattleOnAreaChangeController(player, nounPartOfCommand, parsingDecision);
        if(battleTrigJSON != null && (boolean) battleTrigJSON.get("status")){

            enemyController.setBattleState(true);
            mcf.SetChangeMusicStatus(true);
            this.playerCommandBeforeBattle = (String) battleTrigJSON.get("actionbeforebattle");
            EnemyModel eligibleEnemy = (EnemyModel) battleTrigJSON.get("enemy");
            this.setEnemyToBattle(eligibleEnemy);
            return eligibleEnemy.getDescription();
        }

        //Switch-case that calls the right action method for the specific verb that the
        //user has typed.
        switch(parsingDecision){
            
            case "direction" :
                DirectionActionModel dam = new DirectionActionModel(nounPartOfCommand, itemList);
                resultMessage = dam.PlayerActionCommand(player);
            break;
        
            case "item" :
                ItemController ic = new ItemController(verbPartOfCommand, nounPartOfCommand, itemList);
                resultMessage = ic.ItemActionCommandProcessController(player, enemyController, this.enemyToBattle);
            break;
                
            case "transaction" :
                TransactionController tc = new TransactionController(areasList, listOfMerchants, nounPartOfCommand, verbPartOfCommand);
                resultMessage = tc.transactionCommandProcessControll(player, docksList, itemList);
            break;

            case "battle" :
                resultMessage = enemyController.battleActionProcessController(player, this.getEnemyToBattle(), this.playerCommandBeforeBattle, itemList, mcf);
            break;

            case "sail" :
                DockYardController dyc = new DockYardController(areasList, itemList);
                resultMessage = dyc.dockYardCommandActionProcess(player, docksList, nounPartOfCommand, verbPartOfCommand);
            break;
                
            case "inspect" :
                TabletItemsController tbc = new TabletItemsController(verbPartOfCommand, nounPartOfCommand, itemList);
                resultMessage = tbc.TabletInspectActionCommand(player);
            break;

        }

        return resultMessage;
    }


    public PlayerModel initNewPlayer(PlayerClassesEnum playerClass, String name, Area startingArea) {
        return playerService.createNewPlayer(playerClass, name, startingArea);
    }


    // =============== DISPLAY METHODS ==================

    public String displayPlayerInventoryWeight(PlayerModel player) {
        NumberFormat formater = new DecimalFormat("#0.00");
        double weight = playerService.calculatingPlayerInventoryItemWeight(player);
        return String.format("%s / 100", formater.format(weight));
    }

    public String displayPlayerGold(PlayerModel player) {
        NumberFormat formater = new DecimalFormat("#0.00");
        return String.format("%s", formater.format(player.getGold()));
    }

    public String displayPlayerLevel(PlayerModel player) {
        return String.format("Level : %d", player.getLevel());
    }


}
