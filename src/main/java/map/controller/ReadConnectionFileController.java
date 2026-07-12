
package map.controller;

import map.ReadAreaConnectionFileModel;
import map.model.AreaModel;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that controls the reading of the area connection file and sets
 * lists of the json content.
 *
 * @author Vasilis Triantaris
 */
public class ReadConnectionFileController {

    //Class data members.

    List<AreaModel> areasList;

    List<AreaModel> currentAreaModel;
    List<AreaModel> nextAreaModel;
    List<String> directions;

    //Constructor with parametre the list of areas.
    public ReadConnectionFileController(List<AreaModel> areaModels){
        this.areasList = areaModels;
        this.currentAreaModel = new ArrayList();
        this.nextAreaModel = new ArrayList();
        this.directions = new ArrayList();
    }

    /**
     * The main method of this controlling class which check the integrity of the
     * file path and sets the three data members currentArea, nextArea and directions
     * after they have been created from the model.
     */
    public void readConnectionFileControllingMethod(){

        ReadAreaConnectionFileModel rcfm = new ReadAreaConnectionFileModel(this.areasList);

        JSONArray connectionEntries = rcfm.parseConnectionEntries();
        this.currentAreaModel = rcfm.getListOfCurrentAreas(connectionEntries);
        this.nextAreaModel = rcfm.getListOfNextAreas(connectionEntries);
        this.directions = rcfm.getAreasDirections(connectionEntries);

    }

}
