package item.model;

import item.interfaces.ItemInterface;
import lombok.Getter;
import lombok.Setter;
import map.model.AreaModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemModel implements ItemInterface, Serializable {

    @Getter
    private List<ItemConnectionModel> itemConnectionsWithArea;

    @Getter @Setter
    private String itemName;

    @Getter @Setter
    private String itemDescription;

    @Getter
    private double itemWeight;

    @Getter @Setter
    private double itemValueInGold;

    //The item value of an item depends on its type. If for example the value is 12
    //and the type is weapon then that means that the weapon does 12 damage.
    @Getter @Setter
    private int itemValue;

    @Getter @Setter
    private int itemHealingPower;

    @Getter @Setter
    private int itemType;
    /*The type of the item can be eather :
     * 1 --> Consumables
     * 2 --> Miscenelous (keys)
     * 3 --> Weapons
     * 4 --> Doors / Gates
     * 5 --> Armor
     * 6 --> Shields
     * 7 --> Tablets
     */

    @Getter @Setter
    private AreaModel itemArea;

    @Getter @Setter
    private String attributeType;

    @Getter @Setter
    private int attributeValue;

    //attribute that declares the way that the door/gate is blocking
    @Getter @Setter
    private String blockingDirection;

    //Constructor that sets and item object type 1 (consumble (potion))
    public ItemModel(String name, String descr, int type, double weight, int value, double cost, int healPower){
        this.itemName = name;
        this.itemDescription = descr;
        this.itemType = type;
        this.itemWeight = weight;
        this.itemValue = value;
        this.itemValueInGold = cost;
        this.itemHealingPower = healPower;

        itemConnectionsWithArea = new ArrayList<>();
        this.itemArea = null;
        this.attributeType = "";
        this.attributeValue = 0;
    }

    //Constructor that sets items type 2 (keys)
    public ItemModel(String name, String descr, int type, double weight, int value, double cost){
        this.itemName = name;
        this.itemDescription = descr;
        this.itemType = type;
        this.itemWeight = weight;
        this.itemValue = value;
        this.itemValueInGold = cost;

        itemConnectionsWithArea = new ArrayList<>();
        this.itemArea = null;
        this.attributeType = "";
        this.attributeValue = 0;
        this.itemHealingPower = 0;
    }

    //Constructor that sets items type 3 (weapon)
    public ItemModel(String name, String descr, int type, double weight, int value, String atr, int atrVal, AreaModel areaModel, double cost){
        this.itemName = name;
        this.itemDescription = descr;
        this.itemType = type;
        this.itemWeight = weight;
        this.itemValue = value;
        this.attributeType = atr;
        this.attributeValue = atrVal;
        this.itemArea = areaModel;
        this.itemValueInGold = cost;

        itemConnectionsWithArea = new ArrayList<>();
        this.itemHealingPower = 0;
    }

    //Constructor that sets items type 4 (Gates)
    public ItemModel(String name, String descr, int type, double weight, int value, double cost, String blockDir){
        this.itemName = name;
        this.itemDescription = descr;
        this.itemType = type;
        this.itemWeight = weight;
        this.itemValue = value;
        this.itemValueInGold = cost;
        this.blockingDirection = blockDir;

        itemConnectionsWithArea = new ArrayList<>();
        this.itemHealingPower = 0;
        this.itemArea = null;
        this.attributeType = "";
        this.attributeValue = 0;
    }

    //Constructor that sets items type 5-6 (armor / shield)
    public ItemModel(String name, String descr, int type, double weight, int value, AreaModel areaModel, double cost){
        this.itemName = name;
        this.itemDescription = descr;
        this.itemType = type;
        this.itemWeight = weight;
        this.itemValue = value;
        this.itemArea = areaModel;
        this.itemValueInGold = cost;

        itemConnectionsWithArea = new ArrayList<>();
        this.attributeType = "";
        this.attributeValue = 0;
        this.itemHealingPower = 0;
    }

    //Constructor that sets items type 7 (tablets)
    public ItemModel(String name, String descr, int type, double weight, AreaModel areaModel){
        this.itemName = name;
        this.itemDescription = descr;
        this.itemType = type;
        this.itemWeight = weight;
        this.itemArea = areaModel;

        this.itemValue = 0;
        this.itemValueInGold = 0.0;
        itemConnectionsWithArea = new ArrayList<>();
        this.attributeType = "";
        this.attributeValue = 0;
        this.itemHealingPower = 0;
    }

    public void addItemConnectionWithAreaToList(ItemConnectionModel icwa){
        this.itemConnectionsWithArea.add(icwa);
    }

    //The interface declares this setter as taking a float, which is not compatible
    //with Lombok's generated setter for a double field.
    @Override
    public void setItemWeight(float weight) {
        this.itemWeight = weight;
    }
}
