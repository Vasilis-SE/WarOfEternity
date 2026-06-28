package GameFileConfiguration;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class configures the background music of the game.
 *
 * @author Vasilis Triantaris
 */
public class MusicConfiguration {

    private String soundFileName;
    private Clip clip;
    private boolean musicStatus;
    private boolean changeMusic;

    public MusicConfiguration(){
    }

    public void SetMusicStatus(boolean status){
        this.musicStatus = status;
    }

    public boolean GetMusicStatus(){
        return this.musicStatus;
    }

    public void SetChangeMusicStatus(boolean status){
        this.changeMusic = status;
    }

    public boolean GetChangeMusicStatus(){
        return this.changeMusic;
    }

    public void SetSoundFilePath(String soundFileName){
        this.soundFileName = soundFileName;
    }

    public void PlaySoundFile(){
        try {
            URL url = MusicConfiguration.class.getResource("/MusicAssets/" + this.soundFileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            this.clip = AudioSystem.getClip();
            this.clip.open(audioIn);
            this.clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
        }
    }

    public void StopMusic(){
        this.clip.close();
        this.clip = null;
    }
}
