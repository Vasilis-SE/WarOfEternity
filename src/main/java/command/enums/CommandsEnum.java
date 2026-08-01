package command.enums;

import lombok.Getter;

@Getter
public enum CommandsEnum {

    // Direction commands
    GO("go", "direction"),
    TRAVEL("travel", "direction"),
    MOVE("move", "direction"),
    RIDE("ride", "direction"),
    JOURNEY("journey", "direction"),

    // Battle commands
    BATTLE("battle", "battle"),
    ATTACK("attack", "battle"),
    CHARGE("charge", "battle"),
    HIT("hit", "battle"),
    STRIKE("strike", "battle"),
    BEAT("beat", "battle"),
    RAID("raid", "battle"),

    // Inspect commands
    INSPECT("inspect", "inspect"),
    EXAMINE("examine", "inspect"),
    LOOK("look", "inspect"),
    REVEAL("reveal", "inspect"),
    READ("read", "inspect"),
    STUDY("study", "inspect"),
    CHECK("check", "inspect"),

    // Item commands
    PICKUP("pick", "item"),
    TAKE("take", "item"),
    GRAB("grab", "item"),
    SEARCH("search", "item"),
    SNATCH("snatch", "item"),
    USE("use", "item"),
    OPEN("open", "item"),
    EQUIP("equip", "item"),

    // Sail Commands
    SAIL("sail", "sail"),
    SHIP("ship", "sail"),
    CRUISE("cruise", "sail"),
    SINK("sink", "sail"),

    // Transaction Commands
    BUY("buy", "transaction"),
    SELL("sell", "transaction"),
    TALK("talk", "transaction"),
    PURCHASE("purchase", "transaction"),
    BARGAIN("bargain", "transaction");



    private final String command;
    private final String type;

    CommandsEnum(String command, String type) {
        this.command = command;
        this.type = type;
    }
}
