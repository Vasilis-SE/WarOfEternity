package map;

import GameFileConfiguration.TextFileProcessing;
import map.model.AreaModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that contain methods that read the AreaConnections json file.
 *
 * @author Vasilis Triantaris
 */
public class ReadAreaConnectionFileModel {

    List<AreaModel> areasList;
    StringBuffer strBufData;

    public ReadAreaConnectionFileModel(List<AreaModel> areaModels){
        this.areasList = areaModels;
        this.strBufData = TextFileProcessing.ReadResource("/DataAccessObjects/GameAreaConnections.json");
    }

    /**
     * Parses the area connections json file.
     *
     * @return Returns a JSON array in which every element is one area connection entry, or an empty array if the file could not be parsed.
     */
    public JSONArray parseConnectionEntries(){
        try{
            return (JSONArray) new JSONParser().parse(this.strBufData.toString());
        }
        catch(ParseException ex){
            return new JSONArray();
        }
    }

    /**
     * The "currentArea" field of each connection entry is referred to the current
     * areas (the area that the player is at a specific time), so it gets all
     * of those and resolves them against the list of areas.
     *
     * @param connectionEntries   The parsed area connection entries.
     * @return Returns the list of current game areas.
     */
    public List<AreaModel> getListOfCurrentAreas(JSONArray connectionEntries){

        List<AreaModel> curAreasList = new ArrayList<>();

        for(Object entry : connectionEntries){
            String currentAreaName = (String) ((JSONObject) entry).get("currentArea");

            //Because a specific area can be used more than once in the connections
            //file then we need to check every area on the areas list to resolve
            //each entry to its matching AreaModel object.
            for(AreaModel eachAreaModel : this.areasList)
                if(eachAreaModel.getAreaName().equalsIgnoreCase(currentAreaName))
                    curAreasList.add(eachAreaModel);
        }

        return curAreasList;
    }


    /**
     * The "nextArea" field of each connection entry is consisted of the next areas (
     * the areas that the player can go to when he is a specific current area).
     *
     * @param connectionEntries   The parsed area connection entries.
     * @return Returns the list of next game areas.
     */
    public List<AreaModel> getListOfNextAreas(JSONArray connectionEntries){

        List<AreaModel> nextAreaModelList = new ArrayList<>();

        for(Object entry : connectionEntries){
            String nextAreaName = (String) ((JSONObject) entry).get("nextArea");

            for(AreaModel eachAreaModel : this.areasList)
                if(eachAreaModel.getAreaName().equalsIgnoreCase(nextAreaName))
                    nextAreaModelList.add(eachAreaModel);
        }

        return nextAreaModelList;
    }


    /**
     * The "direction" field of each connection entry is the direction
     * for the specific current area that the user is in.
     *
     * @param connectionEntries   The parsed area connection entries.
     * @return Returns the list of directions.
     */
    public List<String> getAreasDirections(JSONArray connectionEntries){
        List<String> directions = new ArrayList<>();

        for(Object entry : connectionEntries)
            directions.add((String) ((JSONObject) entry).get("direction"));

        return directions;
    }


}
