package map.service;

import utils.TextFileProcessing;
import map.model.AreaConnectionModel;
import map.model.AreaModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that reads the GameAreas and GameAreaConnections json files and
 * builds up the list of game areas along with their connections.
 *
 * @author Vasilis Triantaris
 */
public class AreaService {

    /**
     * Method that reads the game areas json file and creates the area objects.
     *
     * @return Returns the list of game areas read from the data file.
     */
    public List<AreaModel> loadAreas(){
        StringBuffer areaBuffer = TextFileProcessing.ReadResource("/DataAccessObjects/GameAreas.json");
        List<AreaModel> areaModelList = new ArrayList<>();

        try{
            JSONArray areaEntries = (JSONArray) new JSONParser().parse(areaBuffer.toString());

            for(Object entry : areaEntries){
                JSONObject areaEntry = (JSONObject) entry;
                areaModelList.add(new AreaModel((String) areaEntry.get("name"),
                        (String) areaEntry.get("description"), (String) areaEntry.get("image")));
            }
        }
        catch(ParseException ex){
        }

        return areaModelList;
    }

    /**
     * Method that reads the game area connections json file and sets on each
     * area its eligible connections with the other areas.
     *
     * @param areaModelList The list of game areas already read from the data file.
     */
    public void setAreaConnections(List<AreaModel> areaModelList){
        StringBuffer connectionsBuffer = TextFileProcessing.ReadResource("/DataAccessObjects/GameAreaConnections.json");

        try{
            JSONArray connectionEntries = (JSONArray) new JSONParser().parse(connectionsBuffer.toString());

            for(Object entry : connectionEntries){
                JSONObject connectionEntry = (JSONObject) entry;

                AreaModel currentArea = getEligibleArea((String) connectionEntry.get("currentArea"), areaModelList);
                AreaModel nextArea = getEligibleArea((String) connectionEntry.get("nextArea"), areaModelList);
                String direction = (String) connectionEntry.get("direction");

                if(currentArea != null)
                    currentArea.addAreaConnection(new AreaConnectionModel(nextArea, direction));
            }
        }
        catch(ParseException ex){
        }
    }

    /**
     * Method that finds the eligible game area for a given area name.
     *
     * @param areaName The name of the area.
     * @param areaModelList The list of game areas.
     * @return Returns the area that matches the given name, or null if none matches.
     */
    private AreaModel getEligibleArea(String areaName, List<AreaModel> areaModelList){

        for(AreaModel eachAreaModel : areaModelList)
            if(eachAreaModel.getAreaName().equalsIgnoreCase(areaName))
                return eachAreaModel;

        return null;
    }

}
