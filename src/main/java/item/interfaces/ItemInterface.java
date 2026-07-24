package item.interfaces;

/**
 * Interface ItemInterface sets the basic characteristics for an object type item. This
 * interface is being inherited by the item model class.
 *
 * @author Vasilis Triantaris
 */
public interface ItemInterface {

    String itemName = "";
    String itemDescription = "";
    double itemWeight = 0;
    double itemCost=0.0;
    int itemValue = 0;
    int itemType = 0;

    public String getItemName();
    public void setItemName(String name);

    public String getItemDescription();
    public void setItemDescription(String desc);

    public int getItemType();
    public void setItemType(int type);

    public double getItemWeight();
    public void setItemWeight(float weight);

    public int getItemValue();
    public void setItemValue(int value);

    public double getItemValueInGold();
    public void setItemValueInGold(double cost);

}