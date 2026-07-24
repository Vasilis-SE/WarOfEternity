package view.enums;

import lombok.Getter;

@Getter
public enum MainGameMessagesEnum {
    PLAYER_DIED_MESSAGE("You Died!, Game Over!"),
    PLAYER_DIED_TITLE("You Died!"),
    EXIT_CONFIRM_MESSAGE("Are you sure you want to exit the game?\nAll your progress will be"
            + "automatacally lost!"),
    EXIT_CONFIRM_TITLE("Exit Game Message Box"),
    VOICE_MODEL_NOT_FOUND("Voice recognition model not found.\n"
            + "Download a Vosk English model from https://alphacephei.com/vosk/models\n"
            + "and extract it to ~/WarOfEternity/vosk-model/"),
    RECOGNITION_UNAVAILABLE_TITLE("Recognition Unavailable"),
    MICROPHONE_ERROR("An error occurred with the microphone."),
    RECOGNITION_ERROR_TITLE("Recognition Error!"),
    ENDING_STORY("And that was the story of the guardian, sent by the order of \n"
            + "edernium. The one that sacrificed his life for the people of \n"
            + "Yeress and brought an end to the unending war that savaged \n"
            + "the land.\n\n"
            + "Not all stories have their happy ending, and that ones is not \n"
            + "finished yet ...\n"),
    ENDING_TITLE("The End");

    private final String message;

    MainGameMessagesEnum(String message) {
        this.message = message;
    }
}
