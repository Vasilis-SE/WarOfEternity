package GameFileConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class that contains methods for reading text resources from the classpath.
 *
 * @author Vasilis Triantaris
 */
public class TextFileProcessing {

    private TextFileProcessing(){
    }

    /**
     * Reads a classpath resource and returns its content as a StringBuffer.
     *
     * @param resourcePath  Absolute classpath path, e.g. "/DataAccessObjects/GameAreas.json"
     * @return StringBuffer with the file content, or null if the resource is not found.
     */
    public static StringBuffer ReadResource(String resourcePath){
        InputStream is = TextFileProcessing.class.getResourceAsStream(resourcePath);
        if (is == null)
            return null;

        StringBuffer sb = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
        }
        return sb;
    }
}
