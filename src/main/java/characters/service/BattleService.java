package characters.service;

import Items.Item;
import Items.ItemConnectionWithArea;
import map.model.AreaConnectionModel;
import characters.model.EnemyModel;
import characters.model.PlayerModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleService {

    public int getRandomEncounterNumber() {
        return new Random().nextInt(100) + 1;
    }

    public JSONObject triggerBattleOnAreaChange(PlayerModel player, int encPercentage, List<Item> items,
                                                String noun, String parseDecision, boolean battleState,
                                                JSONArray jsonEnemiesArray) {
        JSONObject jObj = new JSONObject();
        boolean status = false;
        EnemyModel eligibleEnemy = null;

        if (noun.equals("sink") && player.getLocation().getAreaName().equals("The Great Jade Sea"))
            eligibleEnemy = getLastBossForFightProcess(jsonEnemiesArray);
        else {
            List<EnemyModel> enemiesOnArea = enemyEncounterAreaIntegrity(player, noun, items, jsonEnemiesArray);
            eligibleEnemy = getRandomEnemyFromListOfEnemiesOnArea(enemiesOnArea, encPercentage);
        }

        if (eligibleEnemy != null && (parseDecision.equals("direction") || parseDecision.equals("sail")) && !battleState)
            status = true;

        jObj.put("status", status);
        jObj.put("actionbeforebattle", noun);
        jObj.put("enemy", eligibleEnemy);

        return jObj;
    }

    public List<EnemyModel> enemyEncounterAreaIntegrity(PlayerModel player, String noun, List<Item> itemList,
                                                        JSONArray jsonEnemiesArray) {
        List<EnemyModel> enemiesOnArea = new ArrayList<>();

        for (AreaConnectionModel acm : player.getLocation().getAreaConnections()) {
            String blockedMessage = directionToNextAreaIsBlockedByItem(itemList, player, noun);
            if (acm.getDirectionsOnCurrentArea().equalsIgnoreCase(noun) && blockedMessage.isEmpty())
                enemiesOnArea = getListOfEnemiesThatRoamTheNextArea(acm, jsonEnemiesArray);
        }

        return enemiesOnArea;
    }

    public String directionToNextAreaIsBlockedByItem(List<Item> itemList, PlayerModel player, String noun) {
        String checkMessage = "";

        for (Item eachItem : itemList) {
            for (ItemConnectionWithArea icwa : eachItem.GetItemConnectionsWithArea()) {
                if ((eachItem.GetItemType() == 4) && icwa.GetItemUsage().equals("open")
                        && (eachItem.GetItemValue() == 0)
                        && eachItem.GetBlockingDirection().equalsIgnoreCase(noun)
                        && player.getLocation().getAreaName().equals(icwa.GetConnectionWithAreaReference().getAreaName()))
                    checkMessage = "You cannot proceed further. The gate is blocking your path!";
            }
        }

        return checkMessage;
    }

    public String attackEnemyProcess(EnemyModel enemyToCombat, PlayerModel player) {
        Random rand = new Random();
        int randomDamage = rand.nextInt(player.getDamage());

        double damageReduction = enemyToCombat.getArmor() / (enemyToCombat.getArmor() + randomDamage);
        int finalDamage = (int) (randomDamage - ((randomDamage * damageReduction) / 2));

        enemyToCombat.setHealth(enemyToCombat.getHealth() - finalDamage);
        String message = "You damaged the enemy for " + finalDamage + " attack damage!";

        if (enemyToCombat.getHealth() <= 0) {
            message = "The enemy is dead!";
            double randomGold = enemyToCombat.getGold() * rand.nextDouble();
            player.setGold(player.getGold() + randomGold);
        }

        return message;
    }

    public String attackFromEnemyToPlayerProcess(EnemyModel enemyToCombat, PlayerModel player) {
        Random rand = new Random();
        int randomDamage = rand.nextInt(enemyToCombat.getDamage());

        double damageReduction = player.getArmor() / (player.getArmor() + enemyToCombat.getDamage());
        int finalDamage = (int) (randomDamage - ((randomDamage * damageReduction) / 2));

        player.setHealth(player.getHealth() - finalDamage);
        String message = enemyToCombat.getName() + " damaged you for " + finalDamage + " damage!";

        if (player.getHealth() <= 0)
            message = "You died!";

        return message;
    }

    private EnemyModel getLastBossForFightProcess(JSONArray jsonEnemiesArray) {
        EnemyModel lastBoss = null;

        for (int i = 0; i < jsonEnemiesArray.size(); i++) {
            JSONObject jObj = (JSONObject) jsonEnemiesArray.get(i);
            if (((String) jObj.get("areaname")).equals("Jade Sea Depths")) {
                List<EnemyModel> enemyListOnArea = (List<EnemyModel>) jObj.get("enemiesonarea");
                lastBoss = enemyListOnArea.getFirst();
            }
        }

        return lastBoss;
    }

    private EnemyModel getRandomEnemyFromListOfEnemiesOnArea(List<EnemyModel> enemiesOnArea, int encPercentage) {
        List<EnemyModel> enemiesOnPercent = new ArrayList<>();

        for (EnemyModel enemy : enemiesOnArea) {
            if (enemy.getEncounterPercent() == 100 || encPercentage >= enemy.getEncounterPercent())
                enemiesOnPercent.add(enemy);
        }

        if (enemiesOnPercent.size() > 1)
            return enemiesOnPercent.get(new Random().nextInt(enemiesOnPercent.size()));
        else if (enemiesOnPercent.size() == 1)
            return enemiesOnPercent.get(0);

        return null;
    }

    private List<EnemyModel> getListOfEnemiesThatRoamTheNextArea(AreaConnectionModel acm, JSONArray jsonEnemiesArray) {
        List<EnemyModel> enemiesOnArea = new ArrayList<>();

        for (int i = 0; i < jsonEnemiesArray.size(); i++) {
            JSONObject arrayJSONObj = (JSONObject) jsonEnemiesArray.get(i);
            if (((String) arrayJSONObj.get("areaname")).equals(acm.getNextAreaModel().getAreaName()))
                enemiesOnArea = (List<EnemyModel>) arrayJSONObj.get("enemiesonarea");
        }

        return enemiesOnArea;
    }
}
