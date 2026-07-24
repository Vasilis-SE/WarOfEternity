package command.controller;

import command.service.CommandParseService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

@Getter
@NoArgsConstructor
public class CommandParserController {

    private String nounCommand = "";
    private String verbCommand = "";

    private final CommandParseService commandParseService = new CommandParseService();

    /**
     * Method that based of the verb of the command that the user has
     * entered decides which type of action to take.
     *
     * @param action The action command that the user has given.
     * @return Returns a word that refers to the specific parser that the verb has been found.
     */
    public String parserControllingMethodForActionDecision(String action) {
        JSONObject parsedCommand = commandParseService.parseCommandAction(action);

        if (parsedCommand.containsKey("noun"))
            nounCommand = (String) parsedCommand.get("noun");

        if (parsedCommand.containsKey("verb"))
            verbCommand = (String) parsedCommand.get("verb");

        return (String) parsedCommand.get("parsingDecision");
    }
}