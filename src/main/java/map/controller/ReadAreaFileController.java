package map.controller;

import map.ReadAreaFileModel;
import map.model.AreaModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that controls the reading of the area file json.
 *
 * @author Triantaris Vasilis
 */
public class ReadAreaFileController {

    /**
     * Method that calls the read file model to get all the data from the
     * GameAreas file and store them into a list of Area objects.
     *
     * @return Returns the list of game areas.
     */
    public List<AreaModel> areaFileControllingMethod(){
        List<AreaModel> areaModelList = new ArrayList<>();

        ReadAreaFileModel rafm = new ReadAreaFileModel();
        boolean setListCheck = rafm.setAreaList();

        //If the processes of makeing the area objects and adding them on the list.
        if(setListCheck)
            areaModelList = rafm.getAreaList();

        return areaModelList;
    }


}
