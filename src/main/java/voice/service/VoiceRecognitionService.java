package voice.service;

import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.File;
import java.io.IOException;

/**
 * Service that loads the offline Vosk speech recognition model and captures
 * a single spoken command from the microphone.
 *
 * @author Vasilis Triantaris
 */
public class VoiceRecognitionService {

    private static final String MODEL_DIRECTORY = System.getProperty("user.home")
            + File.separator + "WarOfEternity" + File.separator + "vosk-model";

    /**
     * Loads the Vosk model from ~/WarOfEternity/vosk-model/. Recognition is
     * silently disabled (returns null) when the model directory is absent
     * or fails to load.
     */
    public Model loadVoiceRecognitionModel(){
        if(!new File(MODEL_DIRECTORY).exists())
            return null;

        try {
            return new Model(MODEL_DIRECTORY);
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Blocks on the microphone until a complete utterance is detected and
     * returns the recognized text.
     */
    public String recognizeSpeechFromMicrophone(Model voskModel) throws LineUnavailableException, IOException {
        try (Recognizer rec = new Recognizer(voskModel, 16000.0f)) {
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine mic = (TargetDataLine) AudioSystem.getLine(info);

            mic.open(format);
            mic.start();

            String recognizedText = "";
            byte[] buffer = new byte[4096];
            while(true){
                int bytesRead = mic.read(buffer, 0, buffer.length);
                if(rec.acceptWaveForm(buffer, bytesRead)){
                    recognizedText = extractRecognizedText(rec.getResult());
                    break;
                }
            }

            mic.stop();
            mic.close();

            return recognizedText;
        }
    }

    private String extractRecognizedText(String resultJson){
        return resultJson.replaceAll(".*\"text\"\\s*:\\s*\"([^\"]*)\".*", "$1").trim();
    }
}
