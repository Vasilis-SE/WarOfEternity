package characters.controller;

import Items.Item;
import map.model.Area;
import map.model.DockYardModel;
import java.util.ArrayList;
import java.util.List;

import characters.*;
import characters.model.MerchantModel;
import characters.model.PlayerModel;
import characters.service.MerchantService;
import characters.service.PlayerService;
import lombok.Getter;
import org.json.simple.JSONObject;

/**
 *
 * @author Vasilis Triantaris
 */
public class TransactionController {

    private final List<Area> listOfGameAreas;
    @Getter
    private List<MerchantModel> listOfMerchants;

    private final String nounPart;
    private final String verbPart;

    //Constructor for reading merchant data.
    public TransactionController(List<Area> areas){
        this.listOfGameAreas = areas;
        
        this.listOfMerchants = new ArrayList(); 
        this.nounPart = "";
        this.verbPart = "";
    }
    
    //Constructor for transaction action command.
    public TransactionController(List<Area> areas, List<MerchantModel> merchants, String noun, String verb){
        this.listOfGameAreas = areas;
        this.listOfMerchants = merchants;
        this.nounPart = noun;
        this.verbPart = verb;
    }

    /**
     * Method that controls the reading of merchant object from the text
     * file. 
     */
    public void setMerchantSectionDataControllingMethod(){
        
        ReadMerchantConnections rmc = new ReadMerchantConnections();
        
        rmc.GetTextFileColumnsToList();
        List<Area> listOfMerchantAreas = rmc.GetAreasAssociatedWithTheMerchants(this.listOfGameAreas);
        this.listOfMerchants = rmc.SetMerchantList(listOfMerchantAreas);
    }
    
    public String transactionCommandProcessControll(PlayerModel player, List<DockYardModel> docksList, List<Item> listOfItems){
    
        String resultMessage;
        String personToContact = this.nounPart.toLowerCase();
        
        if(personToContact.contains("doctor") || personToContact.contains("healer")){
            DoctorActionModel dam = new DoctorActionModel();
            resultMessage = dam.TalkingToDoctorProcess(player);
        }
        else if(personToContact.contains("captain") || personToContact.contains("fisher")){
            CaptainActionModel cam = new CaptainActionModel(docksList);
            JSONObject jObj = cam.TalkToCaptainProcess(player);
            resultMessage = (String) jObj.get("message");
        }
        else if(((!this.verbPart.equals("sell")) || (!this.verbPart.equals("buy"))) && (personToContact.contains("merchant") || personToContact.contains("merchandise"))){
            MerchantActionModel mam = new MerchantActionModel(this.verbPart, this.nounPart, listOfItems, this.listOfGameAreas, this.listOfMerchants, new MerchantService(new PlayerService()));
            resultMessage = mam.SetMerchantItemListToBeDisplayed(player);
        }
        else if(this.verbPart.equals("buy")){
            MerchantActionModel mam = new MerchantActionModel(this.verbPart, this.nounPart, listOfItems, this.listOfGameAreas, this.listOfMerchants, new MerchantService(new PlayerService()));
            JSONObject jObj = mam.PlayersLocationCanStartATransaction(player.getLocation().getAreaName());
            BuyActionModel bam = new BuyActionModel(this.nounPart, new PlayerService());
            resultMessage = bam.BuyItemFromMerchantProcess(player, jObj);
        }
        else if(this.verbPart.equals("sell")){
            MerchantActionModel mam = new MerchantActionModel(this.verbPart, this.nounPart, listOfItems, this.listOfGameAreas, this.listOfMerchants, new MerchantService(new PlayerService()));
            JSONObject jObj = mam.PlayersLocationCanStartATransaction(player.getLocation().getAreaName());
            SellActionModel sam = new SellActionModel(this.nounPart, new PlayerService());
            resultMessage = sam.SellItemToMerchantProcess(player, jObj);
        }
        else{
            resultMessage = "There is no such transaction / person to contact!";
        }
        
        
        return resultMessage;
    }

}
