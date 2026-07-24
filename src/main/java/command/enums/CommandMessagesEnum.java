package command.enums;

import lombok.Getter;

@Getter
public enum CommandMessagesEnum {
    UNCLEAR_COMMAND("Unclear command given!"),
    VERB_REQUIRED("The first word of a command must be a verb!");

    private final String message;

    CommandMessagesEnum(String message) {
        this.message = message;
    }
}
