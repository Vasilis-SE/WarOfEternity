package serialization.service;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Resolves paths inside the {@code %USERPROFILE%\WarOfEternity\Saves} folder.
 *
 * @author Vasilis Triantaris
 */
final class SaveFilePathResolver {

    private SaveFilePathResolver() {
    }

    static String resolveSavesFolderPath() {
        return resolve("");
    }

    static String resolveSaveFilePath(String fileName) {
        return resolve(fileName);
    }

    private static String resolve(String fileName) {
        String sep = File.separator;
        String usersHome = System.getProperty("user.home");
        String rawPath = usersHome + sep + "WarOfEternity" + sep + "Saves" + (fileName.isEmpty() ? "" : sep + fileName);

        return URLDecoder.decode(rawPath, StandardCharsets.UTF_8).replace("%20", " ");
    }
}