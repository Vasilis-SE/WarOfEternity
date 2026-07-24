package view.enums;

import lombok.Getter;

@Getter
public enum StartGuiMessagesEnum {
    EXIT_CONFIRM_MESSAGE("Are you sure you want to exit the game?"),
    EXIT_CONFIRM_TITLE("Exit Game Message Box"),
    SAVE_SUCCESS("The game has been saved!"),
    SAVE_PROMPT_TITLE("Save Message Prompt"),
    SAVE_ERROR("An error occurred while trying to save your data!\nPlease try again later, or try to reboot the game!"),
    ERROR_PROMPT_TITLE("Error Message Prompt");

    private final String message;

    StartGuiMessagesEnum(String message) {
        this.message = message;
    }
}
