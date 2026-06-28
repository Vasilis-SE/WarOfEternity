package characters;

import GameFileConfiguration.TextFileProcessing;
import Map.Area;
import characters.model.EnemyModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Class ReadEnemyDataModel handles the reading of the file that contains the
 * enemy data and also creates the enemy list to be used by the application.
 * 
 * @author Vasilis Triantaris
 */
public final class ReadEnemyDataModel {

    private JSONArray jsonEnemiesArray;
    private final List<Area> gameAreas;
    
    public ReadEnemyDataModel(List<Area> areas){
        this.jsonEnemiesArray = new JSONArray();
        this.gameAreas = areas;
    }

    /**
     * Method that splits the string buffer into lines.
     * 
     * @param strBuf The string buffer that will be splitted.
     * @return Returns an array of string each string is a line fron the text file.
     */
    private String[] SplitStringBufferDataToLines(StringBuffer strBuf){
        return strBuf.toString().split("\n");
    }
    
    /**
     * Converting the string field to integer.
     * 
     * @param data  The data to be converted.
     * @return Returns the converted data to integer format type.
     */
    private int ConvertStringToInteger(String data){
        int integerData = 0;   
        
        try{
           integerData = Integer.parseInt(data);
        }
        catch(NumberFormatException ex){
        }
        return integerData;
    }
    
    /**
     * Converting a string data type to double.
     * 
     * @param data  The data to be converted.
     * @return Returns the converted data to double data type.
     */
    private double ConvertStringToDouble(String data){
        double doubleData = 0.0;
        
        try{
           doubleData = Double.parseDouble(data);
        }
        catch(NumberFormatException ex){ 
        }
        
        return doubleData;
    }
    
    /**
     * Method that creates json object that hold the name of the area and the 
     * appropriate enemies that roam it.
     * 
     * @param eachArea Each area on the loop.
     * @param dataOnLines Array of string, each string is a line from the enemy file.
     * @return Returns a JSON object which holds the enemy data of the area.
     */
    private JSONObject SetGroupOfEnemiesOnSpecificArea(Area eachArea, String[] dataOnLines){
        JSONObject jObj = new JSONObject();
        List<EnemyModel> listOfEnemiesOnArea = new ArrayList<>();

        for(String line : dataOnLines) {
            if(!line.contains(eachArea.GetAreasName())) continue;
            String[] lineFields = line.split("@");
            listOfEnemiesOnArea.add(EnemyModel.builder()
                    .name(lineFields[0].trim())
                    .description(lineFields[1].trim())
                    .location(eachArea)
                    .damage(this.ConvertStringToInteger(lineFields[3].trim()))
                    .armor(this.ConvertStringToInteger(lineFields[4].trim()))
                    .health(this.ConvertStringToInteger(lineFields[5].trim()))
                    .gold(this.ConvertStringToDouble(lineFields[6].trim()))
                    .experience(this.ConvertStringToInteger(lineFields[7].trim()))
                    .image(lineFields[8].trim())
                    .encounterPercent(this.ConvertStringToInteger(lineFields[9].trim()))
                    .build());
        }
 
        jObj.put("areaname", eachArea.GetAreasName());
        jObj.put("enemiesonarea", listOfEnemiesOnArea);
        
        return jObj;
    }
    
    /**
     * Method that handles the reading and the initializing of the enemies for 
     * the game.
     */
    public void SetEnemiesFromData(){
        StringBuffer enemyBuffer = TextFileProcessing.ReadResource("/DataAccessObjects/GameEnemies.txt");
        String[] dataOnLines = SplitStringBufferDataToLines(enemyBuffer);

        for(Area eachArea : this.gameAreas){
            JSONObject jObj = this.SetGroupOfEnemiesOnSpecificArea(eachArea, dataOnLines);
            this.jsonEnemiesArray.add(jObj);
        }
    }
    
    //Returns the JSON enemy array.
    public JSONArray GetJSONEnemiesArray(){
        return this.jsonEnemiesArray;
    }
}
