package utils;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.swing.JOptionPane;

/**
 * This class configures the folder that the saves of the game will reside.
 * Its locates on the user folder of each personal computer and its compatible
 * with every operating system. If the OS has specific security permissions
 * that forbid the creation of a folder then a exception will be made and shown
 * to the user, after that the application closes.
 *
 * @author Vasilis Triantarhs
 */
public class SaveFolderConfig {

    //Constructor
    public SaveFolderConfig(){
        configureSaveFolder();
    }

    /**
     * Method that configures the save folder inside the home directory.
     */
    private void configureSaveFolder(){
        File theDir = new File(resolveSaveFolderPath());

        if (theDir.exists())
            return;

        try{
            theDir.mkdir();
        }
        catch(SecurityException se){
            JOptionPane.showMessageDialog(null, "Error Occurred!",
                    "Save folder could not be created due to OS permitions,\n"
                    +"Real message : "+se, JOptionPane.OK_OPTION);
            System.exit(0);
        }
    }

    private String resolveSaveFolderPath(){
        String usersHome = System.getProperty("user.home");
        String rawPath = usersHome + File.separator + "WarOfEternity" + File.separator + "Saves";

        return URLDecoder.decode(rawPath, StandardCharsets.UTF_8).replace("%20", " ");
    }
}
