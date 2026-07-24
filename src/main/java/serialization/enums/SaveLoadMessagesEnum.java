package serialization.enums;

import lombok.Getter;

@Getter
public enum SaveLoadMessagesEnum {
    DELETE_SUCCESS("File has been successfully deleted!"),
    DELETE_FAILURE("Error occurred!, Could not delete file!");

    private final String message;

    SaveLoadMessagesEnum(String message) {
        this.message = message;
    }
}
