package command.service;

import command.enums.CommandMessagesEnum;
import command.enums.CommandsEnum;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

import java.util.Set;

@NoArgsConstructor
public class CommandParseService {

    private static final Set<String> SINGLE_WORD_ACTION_TYPES = Set.of("battle", "item", "sail", "inspect");

    /**
     * Parses the player command and decides which action category it belongs to.
     * When a recognized verb is found, the resolved "verb" and "noun" parts of the
     * command are included in the result as well.
     *
     * @param action The action command that the user has given.
     * @return A JSONObject containing the "parsingDecision" and, when resolved, the "verb" and "noun".
     */
    public JSONObject parseCommandAction(String action) {
        String[] splitPlayerAction = action.split(" ");
        boolean isMultiWordCommand = splitPlayerAction.length > 1;
        String actionType = decideActionType(splitPlayerAction);

        JSONObject result = new JSONObject();
        result.put("parsingDecision", decideParsingDecision(actionType, isMultiWordCommand));

        if (!actionType.isEmpty()) {
            result.put("verb", splitPlayerAction[0].trim());
            result.put("noun", isMultiWordCommand ? createNounFromCommandWords(splitPlayerAction) : splitPlayerAction[0].trim());
        } else if (!isMultiWordCommand) {
            result.put("noun", splitPlayerAction[0].trim());
        }

        return result;
    }

    private String decideParsingDecision(String actionType, boolean isMultiWordCommand) {
        if (isMultiWordCommand)
            return actionType.isEmpty() ? CommandMessagesEnum.VERB_REQUIRED.getMessage() : actionType;

        if (actionType.isEmpty())
            return "direction";

        return SINGLE_WORD_ACTION_TYPES.contains(actionType) ? actionType : CommandMessagesEnum.UNCLEAR_COMMAND.getMessage();
    }

    /**
     * Determines the action category of a command by matching its verb against the
     * known command list.
     *
     * @param splitPlayerAction The player command split into words.
     * @return The action type of the matched verb, or an empty string if none matched.
     */
    private String decideActionType(String[] splitPlayerAction) {
        for (CommandsEnum eachCommand : CommandsEnum.values())
            if (eachCommand.getCommand().trim().equalsIgnoreCase(splitPlayerAction[0]))
                return eachCommand.getType();

        return "";
    }

    /**
     * Builds the noun part of a command from every word after the verb.
     *
     * @param splitAction The player command split into words.
     * @return The joined noun part of the command.
     */
    private String createNounFromCommandWords(String[] splitAction) {
        StringBuilder nounPart = new StringBuilder();

        for (int i = 1; i < splitAction.length; i++)
            nounPart.append(splitAction[i]).append(" ");

        return nounPart.toString().trim();
    }
}