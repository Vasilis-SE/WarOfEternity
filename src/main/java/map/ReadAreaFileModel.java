package map;

import GameFileConfiguration.TextFileProcessing;
import map.model.AreaModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;


public class ReadAreaFileModel {

    private final List<AreaModel> areaModelList;
    private final StringBuffer strBuff;

    //Simple Constructor
    public ReadAreaFileModel(){
        this.areaModelList = new ArrayList<>();
        this.strBuff = TextFileProcessing.ReadResource("/DataAccessObjects/GameAreas.json");
    }

    /**
     * Method that reads the game areas json file and creates the area objects.
     *
     * @return Returns a boolean variable that reports whether the area list creation went fine.
     */
    public boolean setAreaList(){
        try{
            JSONArray areaEntries = (JSONArray) new JSONParser().parse(this.strBuff.toString());

            for(Object entry : areaEntries){
                JSONObject areaEntry = (JSONObject) entry;
                this.areaModelList.add(new AreaModel((String) areaEntry.get("name"),
                        (String) areaEntry.get("description"), (String) areaEntry.get("image")));
            }
        }
        catch(ParseException ex){
            return false;
        }

        return true;
    }

    //Method that return the list of areas of the game.
    public List<AreaModel> getAreaList(){
        return this.areaModelList;
    }
}
