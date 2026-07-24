package map.service;

import GameFileConfiguration.TextFileProcessing;
import map.model.AreaModel;
import map.model.DockYardModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that reads the DockYardConnections json file and builds up the
 * list of dock yard connections used by the game.
 *
 * @author Vasilis Triantaris
 */
public class DockYardConnectionService {

    /**
     * Method that reads the dock yard connections json file and builds the
     * list of dock yard connections between the given game areas.
     *
     * @param areaModels The list of game areas.
     * @return Returns the list of dock yard connections read from the data file.
     */
    public List<DockYardModel> loadDockYards(List<AreaModel> areaModels) {
        StringBuffer dockYardFileBuffer = TextFileProcessing.ReadResource("/DataAccessObjects/DockYardConnections.json");
        List<DockYardModel> listOfDockYards = new ArrayList<>();

        try {
            JSONArray dockYardEntries = (JSONArray) new JSONParser().parse(dockYardFileBuffer.toString());

            for (Object entry : dockYardEntries) {
                JSONObject dockYardEntry = (JSONObject) entry;

                listOfDockYards.add(DockYardModel.builder()
                        .startingDockLocation(getEligibleArea((String) dockYardEntry.get("startingArea"), areaModels))
                        .destinationDockLocation(getEligibleArea((String) dockYardEntry.get("destinationArea"), areaModels))
                        .sailingFee(((Number) dockYardEntry.get("sailingFee")).doubleValue())
                        .build());
            }
        } catch (ParseException ex) {
        }

        return listOfDockYards;
    }

    /**
     * Method that finds the eligible game area for a given area name.
     *
     * @param areaName The name of the area.
     * @param areaModels The list of game areas.
     * @return Returns the area that matches the given name, or null if none matches.
     */
    private AreaModel getEligibleArea(String areaName, List<AreaModel> areaModels) {
        for (AreaModel eachAreaModel : areaModels)
            if (eachAreaModel.getAreaName().equals(areaName))
                return eachAreaModel;

        return null;
    }

}