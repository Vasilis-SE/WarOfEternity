package characters.controller;

import utils.MusicConfiguration;
import item.model.ItemModel;
import item.controller.ItemController;
import item.controller.TabletItemsController;
import map.controller.DirectionController;
import map.controller.DockYardController;
import map.model.AreaModel;
import map.model.DockYardModel;
import characters.enums.PlayerClassesEnum;
import characters.model.EnemyModel;
import characters.model.MerchantModel;
import characters.model.PlayerModel;
import characters.service.PlayerService;
import lombok.AllArgsConstructor;
import lombok.Getter;
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


    public String playerMainControllingMethodForActionDecision(PlayerModel player, List<ItemModel> itemList,
                                                               BattleController enemyController, List<AreaModel> areasList, List<DockYardModel> docksList,
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
            mcf.setChangeMusicStatus(true);
            this.playerCommandBeforeBattle = (String) battleTrigJSON.get("actionbeforebattle");
            EnemyModel eligibleEnemy = (EnemyModel) battleTrigJSON.get("enemy");
            this.setEnemyToBattle(eligibleEnemy);
            return eligibleEnemy.getDescription();
        }

        //Switch-case that calls the right action method for the specific verb that the
        //user has typed.
        switch(parsingDecision){
            
            case "direction" :
                DirectionController dc = new DirectionController(nounPartOfCommand, itemList);
                resultMessage = dc.playerActionCommand(player);
            break;
        
            case "item" :
                ItemController ic = new ItemController(verbPartOfCommand, nounPartOfCommand, itemList);
                resultMessage = ic.itemActionCommandProcessController(player, enemyController, this.enemyToBattle);
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
                resultMessage = tbc.tabletInspectActionCommand(player);
            break;

        }

        return resultMessage;
    }


    public PlayerModel initNewPlayer(PlayerClassesEnum playerClass, String name, AreaModel startingAreaModel) {
        return playerService.createNewPlayer(playerClass, name, startingAreaModel);
    }

    public boolean isPlayerDead(PlayerModel player) {
        return playerService.isPlayerDead(player);
    }

    public boolean hasPlayerReachedFinalArea(PlayerModel player) {
        return playerService.hasPlayerReachedFinalArea(player);
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
