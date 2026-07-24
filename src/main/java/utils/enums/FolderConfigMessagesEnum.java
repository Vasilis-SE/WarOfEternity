package utils.enums;

import lombok.Getter;

@Getter
public enum FolderConfigMessagesEnum {
    ERROR_OCCURRED("Error Occurred!"),
    FOLDER_CREATION_FAILED("Save folder could not be created due to OS permitions,\nReal message : %s");

    private final String message;

    FolderConfigMessagesEnum(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
