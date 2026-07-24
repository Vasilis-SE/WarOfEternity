package map.service;

import GameFileConfiguration.TextFileProcessing;
import lombok.Getter;
import map.model.AreaModel;
import map.model.DockYardModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that reads the DockYardConnections text file and builds up the
 * list of dock yard connections used by the game.
 *
 * @author Vasilhs Triantarhs
 */
public class DockYardConnectionService {

    @Getter
    private List<DockYardModel> dockYardList;
    private StringBuffer docksFileBuffer;

    @Getter
    private List<String> startAreaStringList;
    @Getter
    private List<String> destinationStringList;
    private List<String> shipFeeList;

    public DockYardConnectionService(){
        this.dockYardList = new ArrayList<>();

        this.startAreaStringList = new ArrayList<>();
        this.destinationStringList = new ArrayList<>();
        this.shipFeeList = new ArrayList<>();

        this.docksFileBuffer = TextFileProcessing.ReadResource("/DataAccessObjects/DockYardConnections.txt");
    }

    /**
     * Splits the string buffer into lines. Each line represents a row into the table.
     *
     * @return Returns a table of string in which every row is a line on the text file.
     */
    private String[] splitTextDataOnLines(){
        return this.docksFileBuffer.toString().split("\n");
    }

    /**
     * Converting a string data type to double.
     *
     * @param data The data to be converted.
     * @return Returns the converted data to double data format.
     */
    private double convertStringToDouble(String data){
        double doubleData = 0.0;

        try{
            doubleData = Double.parseDouble(data);
        }
        catch(NumberFormatException ex){
        }

        return doubleData;
    }

    /**
     * Splits each line from the text file into individual fields and adds
     * each field into the proper list on string data type.
     */
    public void getTextFileColumnsToList(){

        String[] dataOnLines = this.splitTextDataOnLines();

        for(String eachLine : dataOnLines){
            String[] splitLineOnFields = eachLine.split("@");

            this.addStartingDockAreaIntoList(splitLineOnFields[0].trim());
            this.addDestinationDockAreaIntoList(splitLineOnFields[1].trim());
            this.addShippingFeeIntoList(splitLineOnFields[2].trim());
        }
    }

    /**
     * Adds a string into the startAreaStringList that represents the name of the starting area.
     *
     * @param data The name of the starting area trimmed.
     */
    private void addStartingDockAreaIntoList(String data){
        this.startAreaStringList.add(data);
    }

    /**
     * Adds a string into the destinationStringList that represents the name of the destination area.
     *
     * @param data The name of the destination area trimmed.
     */
    private void addDestinationDockAreaIntoList(String data){
        this.destinationStringList.add(data);
    }

    /**
     * Adds a string into the shipFeeList that represents the amount of fee
     * the player has to pay in order to use the ship.
     *
     * @param data The amount of the shipping fee trimmed.
     */
    private void addShippingFeeIntoList(String data){
        this.shipFeeList.add(data);
    }

    /**
     * Determines which area objects are used in every specific string area list. It
     * takes the string list of dock areas that can either be the starting dock areas
     * or the destination dock areas and the list of areas.
     *
     * @param stringAreaList The string list of dock areas. It can either be starting dock areas or destination dock areas.
     * @param areasList The list of game areas.
     * @return Returns a list of areas that are either the starting dock areas or the destination dock areas.
     */
    public List<AreaModel> getDockAreaList(List<String> stringAreaList, List<AreaModel> areasList){
        List<AreaModel> dockAreaModels = new ArrayList<>();

        for(String eachStringArea : stringAreaList){
            for(AreaModel eachAreaModel : areasList){

                if(eachAreaModel.getAreaName().equals(eachStringArea.trim()))
                    dockAreaModels.add(eachAreaModel);
            }
        }

        return dockAreaModels;
    }

    /**
     * Creates the connection of dock yards from the two separated area lists
     * and the shipping fee list. At the end it adds all the dock yard objects
     * into a list.
     *
     * @param start The starting dock yard locations.
     * @param dest The destination dock yard locations.
     */
    public void setDockYardConnectionsToList(List<AreaModel> start, List<AreaModel> dest){

        for(int i=0; i < this.shipFeeList.size(); i++){
            DockYardModel dockYard = DockYardModel.builder()
                    .startingDockLocation(start.get(i))
                    .destinationDockLocation(dest.get(i))
                    .sailingFee(this.convertStringToDouble(this.shipFeeList.get(i)))
                    .build();

            this.addDockYardObjectToTheList(dockYard);
        }
    }

    /**
     * Adds a dock yard object into the dock yard list.
     *
     * @param obj The dock yard object.
     */
    private void addDockYardObjectToTheList(DockYardModel obj){
        this.dockYardList.add(obj);
    }

}
