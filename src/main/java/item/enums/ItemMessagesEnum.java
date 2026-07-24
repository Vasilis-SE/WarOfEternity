package item.enums;

import lombok.Getter;

@Getter
public enum ItemMessagesEnum {
    ITEM_NOT_IN_INVENTORY_FOR_USE("There is no such item in your inventory!"),
    CANNOT_BE_EQUIPPED("This item cant be equiped!"),
    ITEM_NOT_IN_INVENTORY_FOR_EQUIP("There no such item in your inventory!"),
    EQUIP_SUCCESS("%s is equiped!"),
    POTION_USED("%s has been used!"),
    GATE_OPENED("Gate has been open!"),
    KEY_ALREADY_USED("Item : %s has already been used!"),
    ITEM_CANNOT_BE_USED_HERE("This item can't be used here!"),
    KEY_WRONG_GATE("The item : %s can't be used here!"),
    SEARCH_NOTHING_FOUND("Nothing found while searching!"),
    SEARCH_RESULTS_HEADER("While searching you found : \n%s"),
    SEARCH_RESULT_LINE("--> %s\n"),
    SEARCH_RESULT_TABLET_LINE("--> A %s"),
    PICK_WEIGHT_LIMIT_EXCEEDED("Exceeding weight limit, can't pick that up!"),
    PICK_MISC_SUCCESS("Item %s is picked.\n--> Description : %s"),
    PICK_ALREADY_PICKED("You have already picked : %s"),
    PICK_CONSUMABLE_SUCCESS("Item %s is picked\n--> Decription : %s"),
    PICK_ITEM_NOT_FOUND("There is no item : %s"),
    TABLET_NONE_TO_INSPECT("There is no stone tablet here to inspect !"),
    TABLET_DESCRIPTION("Tablet Description :\n%s");

    private final String message;

    ItemMessagesEnum(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
