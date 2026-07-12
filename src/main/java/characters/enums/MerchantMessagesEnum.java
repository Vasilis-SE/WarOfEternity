package characters.enums;

import lombok.Getter;

@Getter
public enum MerchantMessagesEnum {
    ITEM_NOT_FOUND("There is no such item!"),
    WEIGHT_LIMIT_EXCEEDED("Exceeding weight limit, can't buy this item!"),
    CANNOT_AFFORD_ITEM("Sorry sir, you cant afford this item..."),
    PURCHASE_THANK_YOU("Thank you, can i do anything else for you sir ?"),
    ITEM_NOT_SELLABLE("I can't buy this, iam sorry..."),
    SALE_ASSISTANCE_OFFER("Can help you with anything else sir ?"),
    ITEM_NOT_OWNED("There is no such item in your inventory!");

    private final String message;

    MerchantMessagesEnum(String message) {
        this.message = message;
    }
}