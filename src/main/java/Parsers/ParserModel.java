package Parsers;

import GameFileConfiguration.TextFileProcessing;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that deals with the reading of the parsers and adding their 
 * content inside lists.
 * 
 * @author Vasilis Triantaris
 */
public class ParserModel {
    
    //Data member of the class
    private List<String> directionParser;
    private List<String> itemParser;
    private List<String> transactionParser;
    private List<String> battleParser;
    private List<String> sailParser;
    private List<String> inspectParser;

    //Simple Constructor
    public ParserModel(){
        this.directionParser = new ArrayList();
        this.itemParser = new ArrayList();
        this.transactionParser = new ArrayList();
        this.battleParser = new ArrayList();
        this.sailParser = new ArrayList();
        this.inspectParser = new ArrayList();
        
        this.SetParserDataForTheGame();
    }
    
    /**
     * Method that handles the reading of all the parsers of the game.
     */
    private void SetParserDataForTheGame(){
    
        StringBuffer directionBuffer = TextFileProcessing.ReadResource("/DataAccessObjects/DirectionsVerbParser.txt");
        StringBuffer itemBuffer = TextFileProcessing.ReadResource("/DataAccessObjects/ItemVerbParser.txt");
        StringBuffer transactionBuffer = TextFileProcessing.ReadResource("/DataAccessObjects/TransactionVerbParser.txt");
        StringBuffer battleBuffer = TextFileProcessing.ReadResource("/DataAccessObjects/BattleParser.txt");
        StringBuffer sailBuffer = TextFileProcessing.ReadResource("/DataAccessObjects/SailParser.txt");
        StringBuffer inspectBuffer = TextFileProcessing.ReadResource("/DataAccessObjects/InspectParser.txt");
        
        //Processes all the verbs in all the parsers.
        this.directionParser = this.SplitStringBufferIntoLines(directionBuffer);
        this.itemParser = this.SplitStringBufferIntoLines(itemBuffer);
        this.transactionParser = this.SplitStringBufferIntoLines(transactionBuffer);
        this.battleParser = this.SplitStringBufferIntoLines(battleBuffer);
        this.sailParser = this.SplitStringBufferIntoLines(sailBuffer);
        this.inspectParser = this.SplitStringBufferIntoLines(inspectBuffer);
    }

    /**
     * Method that splits each line of the parser into a single entity.
     * 
     * @param stringBuffer
     * @return 
     */
    private List<String> SplitStringBufferIntoLines(StringBuffer stringBuffer){
        List<String> parserList = new ArrayList();
        
        String[] dataOnLines = stringBuffer.toString().split("\n");
        for (String dataOnLine : dataOnLines) {
            parserList.add(dataOnLine.trim());
        }
        
        return parserList;
    }

    /**
     * Split the command of the player.
     *
     * @param command   The action command that the user has given.
     * @return Returns an array in which there are two cell the first for the verb part and the second for the noun part.
     */
    public String[] SplitPlayerCommand(String command){
        String[] commandIndex = command.split(" ");
        return commandIndex;
    }
    
    //Returns the list of the verbs of the direction parser text file.
    public List<String> GetListOfDirectionParser(){
        return this.directionParser;
    }
    
    //Returns the list of the verbs of the item parser text file.
    public List<String> GetListOfItemParser(){
        return this.itemParser;
    }
    
    //Returns the list of the verbs of the transaction parser text file.
    public List<String> GetListOfTransactionParser(){
        return this.transactionParser;
    }
    
    //Returns the list of the verbs of the transaction parser text file.
    public List<String> GetListOfBattleParser(){
        return this.battleParser;
    }
    
    //Returns the list of the verbs of the sail parser text file.
    public List<String> GetListOfSailParser(){
        return this.sailParser;
    }
    
    //Returns the parser of the inspect text file.
    public List<String> GetListOfInspectParser(){
        return this.inspectParser;
    }
}
