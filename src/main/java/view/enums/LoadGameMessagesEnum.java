package view.enums;

import lombok.Getter;

@Getter
public enum LoadGameMessagesEnum {
    DELETE_PROMPT_TITLE("Delete Message Prompt"),
    NO_FILE_SELECTED("Error Occured!, You need to select a specific\nfile before loading..."),
    NO_FILE_SELECTED_TITLE("Errom Message Prompt"),
    LOAD_IO_ERROR("Error occurred, while loading the save file.\nPlease try again later!"),
    LOAD_CLASS_ERROR("Error occurred, cant load the specific \nfile. Please try again later or reboot the game!"),
    LOAD_ERROR_TITLE("Load Error Message Prompt");

    private final String message;

    LoadGameMessagesEnum(String message) {
        this.message = message;
    }
}
