package voice.controller;

import org.vosk.Model;
import voice.service.VoiceRecognitionService;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

/**
 * Controlling class for the optional offline voice recognition feature.
 *
 * @author Vasilis Triantaris
 */
public class VoiceRecognitionController {

    private final VoiceRecognitionService voiceRecognitionService = new VoiceRecognitionService();

    public Model loadVoiceRecognitionModel(){
        return voiceRecognitionService.loadVoiceRecognitionModel();
    }

    public String recognizeSpeechFromMicrophone(Model voskModel) throws LineUnavailableException, IOException {
        return voiceRecognitionService.recognizeSpeechFromMicrophone(voskModel);
    }
}
