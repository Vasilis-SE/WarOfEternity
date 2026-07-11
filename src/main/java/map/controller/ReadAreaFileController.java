package map.controller;

import map.ReadAreaFileModel;
import map.model.Area;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that controls the reading of the area file text.
 * 
 * @author Triantaris Vasilis
 */
public class ReadAreaFileController {
    
    private StringBuffer strBufData;
    
    //Simple Constructor
    public ReadAreaFileController(){
        this.strBufData = null;
    }
    
    /**
     * Method that calls the read file model to get all the data from the 
     * GameAreas file and store them into a list of Area objects.
     * 
     * @return Returns the list of game areas.
     */
    public List<Area> areaFileControllingMethod(){
        List<Area> areaList = new ArrayList();

        ReadAreaFileModel rafm = new ReadAreaFileModel();

        String[] dataOnLines = rafm.splitStringBufferDataToLines();
        boolean setListCheck = rafm.setAreaList(dataOnLines);

        //If the processes of makeing the area objects and adding them on the list.
        if(setListCheck)
            areaList = rafm.getAreaList();

        return areaList;
    }
    
    
}
