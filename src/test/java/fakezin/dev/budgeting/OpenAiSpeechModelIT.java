package fakezin.dev.budgeting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiSpeechModelIT {

    @Autowired
    private OpenAiAudioSpeechModel openAiAudioSpeechModel;

    @Test
    public void should_producePortugueseAudio_when_textIsProvided() throws IOException {
        String textToSpeak = "O valor total do serviço ficou em 80 reais. Posso confirmar o seu pagamento?";

        byte[] audioBytes = openAiAudioSpeechModel.call(textToSpeak);

        // Asserções
        assertThat(audioBytes).isNotNull();
        assertThat(audioBytes).isNotEmpty();
        assertThat(audioBytes.length).isGreaterThan(1024);

        // Grava o arquivo MP3 gerado
        Path tempFile = Files.createTempFile("AUDIO_OPENAI_PTBR_", ".mp3");
        Files.write(tempFile, audioBytes);

        System.out.println("Áudio em Português gerado com sucesso pela OpenAI em: " + tempFile.toAbsolutePath());
    }
}