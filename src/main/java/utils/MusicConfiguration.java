package utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    // On Linux, javax.sound.sampled only talks to ALSA directly, which on a
    // stock PipeWire setup (no pipewire-alsa compatibility plugin) silently
    // produces no audible output. pw-play talks to PipeWire natively, so it's
    // used there instead. Windows/macOS are unaffected and keep using Clip.
    private static final boolean USE_NATIVE_PIPEWIRE_PLAYER =
            System.getProperty("os.name", "").toLowerCase().contains("linux");

    // Playback/stop calls are dispatched here instead of the Swing EDT, since
    // both AudioSystem.getClip()/open()/start()/close() and starting/killing
    // the pw-play process are blocking calls that would otherwise freeze the
    // whole GUI. Single-threaded so play/stop requests stay ordered relative
    // to each other.
    private final ExecutorService audioExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "music-playback");
        t.setDaemon(true);
        return t;
    });

    private String soundFileName;
    private volatile Clip clip;
    private volatile Process playbackProcess;
    private boolean musicStatus;
    private boolean changeMusic;

    private static final String OUTDOOR_TRACK = "outdoor1.wav";
    private static final String COMBAT_TRACK = "combat.wav";

    public MusicConfiguration(){
    }

    public void setMusicStatus(boolean status){
        this.musicStatus = status;
    }

    public boolean getMusicStatus(){
        return this.musicStatus;
    }

    public void setChangeMusicStatus(boolean status){
        this.changeMusic = status;
    }

    public boolean getChangeMusicStatus(){
        return this.changeMusic;
    }

    public void setSoundFilePath(String soundFileName){
        this.soundFileName = soundFileName;
    }

    public void playSoundFile(){
        final String fileToPlay = this.soundFileName;
        audioExecutor.submit(() -> {
            if (USE_NATIVE_PIPEWIRE_PLAYER)
                playViaPipeWire(fileToPlay);
            else
                playViaJavaSound(fileToPlay);
        });
    }

    public void stopMusic(){
        audioExecutor.submit(() -> {
            if (this.playbackProcess != null) {
                this.playbackProcess.destroy();
                this.playbackProcess = null;
            }
            if (this.clip != null) {
                this.clip.close();
                this.clip = null;
            }
        });
    }

    /**
     * Toggles the music on/off, picking the track that matches the current
     * battle state whenever it is turned back on.
     *
     * @param battleState Whether the player is currently in a battle.
     */
    public void toggleMusicForCurrentState(boolean battleState){
        if(this.musicStatus){
            setMusicStatus(false);
            stopMusic();
        } else {
            setSoundFilePath(battleState ? COMBAT_TRACK : OUTDOOR_TRACK);
            setMusicStatus(true);
            playSoundFile();
        }
    }

    /**
     * Switches the playing track to match the battle state after a player
     * command has been executed, if a change was flagged.
     *
     * @param battleState Whether the player is currently in a battle.
     */
    public void applyPostActionMusicChange(boolean battleState){
        if(!this.changeMusic)
            return;

        stopMusic();
        setSoundFilePath(battleState ? COMBAT_TRACK : OUTDOOR_TRACK);
        setMusicStatus(true);

        if(!battleState)
            setChangeMusicStatus(false);

        playSoundFile();
    }

    private void playViaJavaSound(String fileToPlay){
        try {
            URL url = MusicConfiguration.class.getResource("/MusicAssets/" + fileToPlay);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip newClip = AudioSystem.getClip();
            newClip.open(audioIn);
            newClip.start();
            this.clip = newClip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Pipes the sound resource straight into `pw-play`'s stdin so it works
    // both run from the classes directory and packaged inside the jar.
    private void playViaPipeWire(String fileToPlay){
        try {
            InputStream soundData = MusicConfiguration.class.getResourceAsStream("/MusicAssets/" + fileToPlay);
            if (soundData == null)
                return;

            Process process = new ProcessBuilder("pw-play", "-")
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
            this.playbackProcess = process;

            // The pipe fills at playback speed, so pumping it here would block
            // this executor for the whole track and stall the next play/stop
            // request behind it. Pump on a throwaway thread instead.
            Thread writer = new Thread(() -> {
                try (soundData; var out = process.getOutputStream()) {
                    soundData.transferTo(out);
                } catch (IOException e) {
                    // Expected when stopMusic() kills the process mid-stream.
                }
            }, "music-writer");
            writer.setDaemon(true);
            writer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
