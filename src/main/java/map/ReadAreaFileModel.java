package map;

import GameFileConfiguration.TextFileProcessing;
import map.model.Area;

import java.util.ArrayList;
import java.util.List;


public class ReadAreaFileModel {
    
    private List<Area> areaList;
    private String areaFilePath;
    private StringBuffer strBuff;
    
    //Simple Constructor
    public ReadAreaFileModel(){
        this.areaFilePath = "";
        this.areaList = new ArrayList();
        this.strBuff = TextFileProcessing.ReadResource("/DataAccessObjects/GameAreas.txt");
    }

    /**
     * Method that splits the string buffer to lines.
     * 
     * @return Returns an array of string in which each line is a line in the GameAreas file.
     */
    public String[] splitStringBufferDataToLines(){
    
        String[] dataOnLines = this.strBuff.toString().split("\n");
        
        return dataOnLines;
    }
    
    /**
     * Method that takes as a parameter the table where in each cell is 
     * contained the string line of the text file.
     * 
     * @param dataOnLines   The array of string that each string is a data line.
     * @return Returns a boolean variable that reports whether the array list data creation went fine.
     */
    public boolean setAreaList(String[] dataOnLines){
        
        try{
            for(int i=0; i<dataOnLines.length; i++){
                //For each line of the text split it using the '@' charachter as a
                //separator.
                String[] lineIndex = dataOnLines[i].split("@");
            
                if(lineIndex.length == 3){
                    //Set tha data of each area object 
                    Area areaObj = new Area(lineIndex[0].trim(), lineIndex[1].trim(), lineIndex[2].trim());
                    this.areaList.add(areaObj);
                }
                //In case the user didnt enter any image
                else{
                    //Set tha data of each area object 
                    Area areaObj = new Area(lineIndex[0].trim(), lineIndex[1].trim(), "");
                    this.areaList.add(areaObj);
                }
            }
        }
        catch(Exception ex){
            return false;
        }
        
        return true;
    }
    
    //Method that return the list of areas of the game.
    public List<Area> getAreaList(){
        return this.areaList;
    }
}
