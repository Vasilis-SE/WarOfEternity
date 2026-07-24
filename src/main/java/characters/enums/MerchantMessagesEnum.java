package characters.enums;

import lombok.Getter;

@Getter
public enum MerchantMessagesEnum {
    GREETING("How can i help you sir?, I have got the best stuff for defence and offence. So what will it be?\n\n"),
    NO_MERCHANT_IN_AREA("There is no merchant in this area!"),
    ITEM_NOT_FOUND("There is no such item!"),
    WEIGHT_LIMIT_EXCEEDED("Exceeding weight limit, can't buy this item!"),
    CANNOT_AFFORD_ITEM("Sorry sir, you cant afford this item..."),
    PURCHASE_THANK_YOU("Thank you, can i do anything else for you sir ?"),
    ITEM_NOT_SELLABLE("I can't buy this, iam sorry..."),
    SALE_ASSISTANCE_OFFER("Can help you with anything else sir ?"),
    ITEM_NOT_OWNED("There is no such item in your inventory!"),
    CONSUMABLE_LISTING("Name : %s\nDescription : %s\nHealing Power : %s\nItem Quantity : 8\nItem Weight : %s kg\nCost : %s gold\n---------------------------------------------\n"),
    ARMOR_LISTING("Name : %s\nDescription : %s\nArmor : %s\nItem Weight : %s kg\nCost : %s gold\n---------------------------------------------\n"),
    WEAPON_STRENGTH_LISTING("Name : %s\nDescription : %s\nStrength : %s\nDamage : %s\nItem Weight : %s kg\nCost : %s gold\n---------------------------------------------\n"),
    WEAPON_AGILITY_LISTING("Name : %s\nDescription : %s\nAgility : %s\nDamage : %s\nItem Weight : %s kg\nCost : %s gold\n---------------------------------------------\n"),
    WEAPON_INTELLIGENCE_LISTING("Name : %s\nDescription : %s\nIntelligence : %s\nDamage : %s\nItem Weight : %s kg\nCost : %s gold\n---------------------------------------------\n");

    private final String message;

    MerchantMessagesEnum(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
