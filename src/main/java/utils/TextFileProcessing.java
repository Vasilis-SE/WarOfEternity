package utils;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Class that contains methods for reading text resources from the classpath.
 *
 * @author Vasilis Triantaris
 */
@UtilityClass
public class TextFileProcessing {

    /**
     * Reads a classpath resource and returns its content as a string.
     *
     * @param resourcePath  Absolute classpath path, e.g. "/DataAccessObjects/GameAreas.json"
     * @return The file content, or an empty string if the resource is not found.
     */
    public String readResource(String resourcePath){
        try (InputStream is = TextFileProcessing.class.getResourceAsStream(resourcePath)) {
            if (is == null)
                return "";

            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return "";
        }
    }
}
