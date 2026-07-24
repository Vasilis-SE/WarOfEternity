
package utils;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.swing.JOptionPane;
import utils.enums.FolderConfigMessagesEnum;

/**
 * This class handles the creation of the main folder (wrapper folder) inside
 * the home directory of the computer.
 *
 * @author Vasilhs Triantaris
 */
public class MainFolderConfig {

    //Constructor
    public MainFolderConfig(){
        configureMainDataFolder();
    }

    /**
     * Method that checks whether the main folder of the game exists in the
     * home directory of the computer. If it does then the operation stops
     * if it doesn't it creates it. There is a possibility to throw security
     * exception, that happens if the OS of the computer denies the application
     * to create an folders.
     */
    private void configureMainDataFolder(){
        File theDir = new File(resolveMainFolderPath());

        if (theDir.exists())
            return;

        try{
            theDir.mkdir();
        }
        catch(SecurityException se){
            JOptionPane.showMessageDialog(null, FolderConfigMessagesEnum.ERROR_OCCURRED.getMessage(),
                    FolderConfigMessagesEnum.FOLDER_CREATION_FAILED.format(se), JOptionPane.OK_OPTION);
            System.exit(0);
        }
    }

    private String resolveMainFolderPath(){
        String usersHome = System.getProperty("user.home");
        String rawPath = usersHome + File.separator + "WarOfEternity";

        return URLDecoder.decode(rawPath, StandardCharsets.UTF_8).replace("%20", " ");
    }
}
