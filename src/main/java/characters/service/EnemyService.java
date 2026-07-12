package characters.service;

import GameFileConfiguration.TextFileProcessing;
import characters.model.EnemyModel;
import map.model.AreaModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class EnemyService {

    /**
     * Method that handles the reading and the initializing of the enemies for
     * the game.
     *
     * @param areaModels The list of game areas.
     * @return Returns a JSON array which holds the enemy data for every area.
     */
    public JSONArray loadEnemies(List<AreaModel> areaModels) {
        JSONArray enemyEntries = readEnemyEntries();

        JSONArray jsonEnemiesArray = new JSONArray();
        for (AreaModel eachAreaModel : areaModels)
            jsonEnemiesArray.add(buildEnemiesForArea(eachAreaModel, enemyEntries));

        return jsonEnemiesArray;
    }

    /**
     * Method that reads and parses the enemy data file.
     *
     * @return Returns a JSON array holding every enemy spawn entry defined in the data file.
     */
    private JSONArray readEnemyEntries() {
        StringBuffer enemyBuffer = TextFileProcessing.ReadResource("/DataAccessObjects/GameEnemies.json");

        try {
            return (JSONArray) new JSONParser().parse(enemyBuffer.toString());
        } catch (ParseException ex) {
            return new JSONArray();
        }
    }

    /**
     * Method that creates a json object that holds the name of the area and the
     * appropriate enemies that roam it.
     *
     * @param eachAreaModel Each area on the loop.
     * @param enemyEntries  The full list of enemy spawn entries read from the data file.
     * @return Returns a JSON object which holds the enemy data of the area.
     */
    private JSONObject buildEnemiesForArea(AreaModel eachAreaModel, JSONArray enemyEntries) {
        JSONObject jObj = new JSONObject();
        List<EnemyModel> listOfEnemiesOnArea = new ArrayList<>();

        for (Object entry : enemyEntries) {
            JSONObject enemyEntry = (JSONObject) entry;
            if (!eachAreaModel.getAreaName().equals(enemyEntry.get("area"))) continue;

            listOfEnemiesOnArea.add(EnemyModel.builder()
                    .name((String) enemyEntry.get("name"))
                    .description((String) enemyEntry.get("description"))
                    .location(eachAreaModel)
                    .damage(((Number) enemyEntry.get("damage")).intValue())
                    .armor(((Number) enemyEntry.get("armor")).intValue())
                    .health(((Number) enemyEntry.get("health")).intValue())
                    .gold(((Number) enemyEntry.get("gold")).doubleValue())
                    .experience(((Number) enemyEntry.get("experience")).intValue())
                    .image((String) enemyEntry.get("image"))
                    .encounterPercent(((Number) enemyEntry.get("encounterPercent")).intValue())
                    .build());
        }

        jObj.put("areaname", eachAreaModel.getAreaName());
        jObj.put("enemiesonarea", listOfEnemiesOnArea);

        return jObj;
    }
}