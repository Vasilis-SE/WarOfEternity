package map.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
public class Area implements Serializable{

    //Data members of the area object
    @Setter
    private String areaName;
    @Setter
    private String areaDescription;
    @Setter
    private String areaImage;
    private List<AreaConnectionMaker> areaConnections;

    public Area(String areaName, String areaDescription, String areaImage){
        this.areaName = areaName;
        this.areaDescription = areaDescription;
        this.areaImage = areaImage;
        this.areaConnections = new ArrayList<>();
    }

    public void addAreaConnection(AreaConnectionMaker areaConnectionMaker){
        areaConnections.add(areaConnectionMaker);
    }
}