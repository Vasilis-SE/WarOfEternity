package map.controller;

import map.model.AreaModel;
import map.model.AreaConnectionModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapController implements Serializable {
    
    //List data member that contains all the objects for each area
    private List<AreaModel> areasList;
    
    /**
     * Constructor of MapCotroller class when a object of this class is made
     * then this constructor automatically sets all the data that is 
     * referred for the map.
     */
    public MapController(){
        this.areasList = new ArrayList();
        
        ReadAreaFileController rafc = new ReadAreaFileController();
        //Get the list of areas.
        this.areasList = rafc.areaFileControllingMethod();

        ReadConnectionFileController rcfcm = new ReadConnectionFileController(this.areasList);
        rcfcm.readConnectionFileControllingMethod();

        List<AreaModel> currentAreaModels = rcfcm.currentAreaModel;
        List<AreaModel> nextAreaModel = rcfcm.nextAreaModel;
        List<String> directions = rcfcm.directions;

        //For every object in the above list call the setAreaConnection method to
        //create the connection of areas.
        for(int i = 0; i< currentAreaModels.size(); i++){
            setAreaConnection(currentAreaModels.get(i), nextAreaModel.get(i), directions.get(i));
        }

    }

    /**
     * Method that creates the connections of each area. After getting the three
     * columns of the are connection file and storing them in lists with the 
     * ReadAreaConnectionFileModel class we get each list item from those lists 
     * and create the connection.
     * 
     * @param currentAreaModel   The starting area or just the area that the player might be located.
     * @param connectionsWithOtherAreas     The next area that the previous is connected with.
     * @param directions     The direction that the player must follow in order to travel from the currentArea to the NextArea.
     */
    private void setAreaConnection(AreaModel currentAreaModel, AreaModel connectionsWithOtherAreas, String directions){

        //Set a area connection with data the next area and the direction to go there.
        AreaConnectionModel acm = new AreaConnectionModel(connectionsWithOtherAreas, directions);

        //Check all the areas on the area list. If the area on the loop is equal
        //to the one current area given in the parametres then set this area as the
        //current - starting area of the connection.
        for(AreaModel currentAreaModelOnTheLoop : this.areasList){

            if(currentAreaModelOnTheLoop.getAreaName().equals(currentAreaModel.getAreaName())){
                currentAreaModelOnTheLoop.addAreaConnection(acm);
            }

        }
    }

    //Method that return the list of areas.
    public List<AreaModel> getAreasList(){
        return this.areasList;
    }

    
}
