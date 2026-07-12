package Items;

import map.model.AreaModel;

import java.io.Serializable;

public class ItemConnectionWithArea implements Serializable {
    
    AreaModel itemAreaModel;
    Item itemRef;
    String itemUsage;
    
    public ItemConnectionWithArea(){
        this.itemRef = null;
        this.itemAreaModel = null;
        this.itemUsage = "";
    }
    
    public ItemConnectionWithArea(AreaModel areaModel, Item item, String purpose){
        this.itemAreaModel = areaModel;
        this.itemRef = item;
        this.itemUsage = purpose;
    }
    
    public void SetItemConnectedToAreaReference(Item item){
        this.itemRef = item;
    }
    
    public Item GetItemConnectedToAreaReference(){
        return this.itemRef;
    }
    
    public void SetConnectionWithAreaReference(AreaModel areaModel){
        this.itemAreaModel = areaModel;
    }
    
    public AreaModel GetConnectionWithAreaReference(){
        return this.itemAreaModel;
    }
    
    public void SetItemUsage(String purpose){
        this.itemUsage = purpose;
    }
    
    public String GetItemUsage(){
        return this.itemUsage;
    }

}
