package characters.controller;

import characters.enums.BattleMessagesEnum;
import utils.MusicConfiguration;
import item.model.ItemModel;
import map.controller.DirectionController;
import characters.model.EnemyModel;
import characters.model.PlayerModel;
import characters.service.BattleService;
import characters.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Class EnemiesController is the controlling class for actions based on enemies / battle.
 *
 * @author Triantaris Vasilis
 */
@RequiredArgsConstructor
public class BattleController implements Serializable {

    private final List<ItemModel> items;
    private final PlayerService playerService;
    private final BattleService battleService;
    private final EnemyController enemyController;

    private boolean battleState;

    public JSONObject battleIntegrityActionCheck(String parsingDecision, String verb) {
        JSONObject jObj = new JSONObject();
        String message = "";
        boolean status = true;

        if (this.battleState) {
            if (!parsingDecision.equals("battle") && !verb.equals("use")) {
                message = BattleMessagesEnum.CANNOT_ACT_WHILE_IN_BATTLE.getMessage();
                status = false;
            }
        }

        if (!this.battleState && parsingDecision.equals("battle")) {
            message = BattleMessagesEnum.NO_ENEMY_TO_ATTACK.getMessage();
            status = false;
        }

        jObj.put("message", message);
        jObj.put("status", status);

        return jObj;
    }

    public JSONObject triggerBattleOnAreaChangeController(PlayerModel player, String noun, String parsingDecision) {
        int encounterPer = battleService.getRandomEncounterNumber();
        return battleService.triggerBattleOnAreaChange(player, encounterPer, this.items, noun, parsingDecision,
                this.battleState, enemyController.getJsonEnemiesArray());
    }

    public String battleActionProcessController(PlayerModel player, EnemyModel eligibleEnemy,
                                                String actionBeforeBattle, List<ItemModel> listOfItems,
                                                MusicConfiguration mcf) {
        String resultMessage = battleService.attackEnemyProcess(eligibleEnemy, player);

        if (!resultMessage.equals(BattleMessagesEnum.ENEMY_DEFEATED.getMessage())) {
            resultMessage += "\n" + battleService.attackFromEnemyToPlayerProcess(eligibleEnemy, player);
            mcf.setChangeMusicStatus(false);
        } else {
            playerService.battleExperienceEarned(player, eligibleEnemy);
            this.setBattleState(false);
            mcf.setChangeMusicStatus(true);

            if (eligibleEnemy.getName().equals("Alzor The Destroyer") && actionBeforeBattle.equals("sink")) {
                player.setLocation(eligibleEnemy.getLocation());
                resultMessage += "\n" + eligibleEnemy.getLocation().getAreaDescription();
            } else {
                DirectionController dc = new DirectionController(actionBeforeBattle, listOfItems);
                resultMessage += "\n" + dc.playerActionCommand(player);
            }
        }

        return resultMessage;
    }

    public void setBattleState(boolean state) {
        this.battleState = state;
    }

    public boolean getBattleState() {
        return this.battleState;
    }
}