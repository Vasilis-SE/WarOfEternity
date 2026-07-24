package map.controller;

import lombok.Getter;
import map.model.AreaModel;
import map.service.AreaService;

import java.io.Serializable;
import java.util.List;

public class MapController implements Serializable {

    //List data member that contains all the objects for each area
    @Getter
    private List<AreaModel> areasList;

    /**
     * Constructor of MapController class. When an object of this class is made
     * then this constructor automatically sets all the data that is
     * referred for the map.
     */
    public MapController(){
        AreaService areaService = new AreaService();

        this.areasList = areaService.loadAreas();
        areaService.setAreaConnections(this.areasList);
    }

}
