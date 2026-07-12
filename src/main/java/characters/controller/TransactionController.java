package characters.controller;

import Items.Item;
import characters.service.DoctorService;
import map.model.AreaModel;
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

    private final List<AreaModel> listOfGameAreaModels;
    @Getter
    private List<MerchantModel> listOfMerchants;

    private final String nounPart;
    private final String verbPart;

    //Constructor for reading merchant data.
    public TransactionController(List<AreaModel> areaModels){
        this.listOfGameAreaModels = areaModels;
        
        this.listOfMerchants = new ArrayList(); 
        this.nounPart = "";
        this.verbPart = "";
    }
    
    //Constructor for transaction action command.
    public TransactionController(List<AreaModel> areaModels, List<MerchantModel> merchants, String noun, String verb){
        this.listOfGameAreaModels = areaModels;
        this.listOfMerchants = merchants;
        this.nounPart = noun;
        this.verbPart = verb;
    }

    /**
     * Method that controls the reading of merchant object from the text
     * file. 
     */
    public void setMerchantSectionDataControllingMethod(){
        this.listOfMerchants = new MerchantService(new PlayerService()).loadMerchants(this.listOfGameAreaModels);
    }
    
    public String transactionCommandProcessControll(PlayerModel player, List<DockYardModel> docksList, List<Item> listOfItems){
    
        String resultMessage;
        String personToContact = this.nounPart.toLowerCase();
        
        if(personToContact.contains("doctor") || personToContact.contains("healer")){
            DoctorService dam = new DoctorService();
            resultMessage = dam.talkToDoctorProcess(player);
        }
        else if(personToContact.contains("captain") || personToContact.contains("fisher")){
            CaptainActionModel cam = new CaptainActionModel(docksList);
            JSONObject jObj = cam.TalkToCaptainProcess(player);
            resultMessage = (String) jObj.get("message");
        }
        else if(((!this.verbPart.equals("sell")) || (!this.verbPart.equals("buy"))) && (personToContact.contains("merchant") || personToContact.contains("merchandise"))){
            MerchantController mc = new MerchantController(listOfItems, this.listOfMerchants, new MerchantService(new PlayerService()));
            resultMessage = mc.talkToMerchantProcess(player);
        }
        else if(this.verbPart.equals("buy")){
            MerchantController mc = new MerchantController(listOfItems, this.listOfMerchants, new MerchantService(new PlayerService()));
            resultMessage = mc.buyItem(player, this.nounPart);
        }
        else if(this.verbPart.equals("sell")){
            MerchantController mc = new MerchantController(listOfItems, this.listOfMerchants, new MerchantService(new PlayerService()));
            resultMessage = mc.sellItem(player, this.nounPart);
        }
        else{
            resultMessage = "There is no such transaction / person to contact!";
        }
        
        
        return resultMessage;
    }

}
