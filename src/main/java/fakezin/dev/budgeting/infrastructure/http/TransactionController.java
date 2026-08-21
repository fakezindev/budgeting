package fakezin.dev.budgeting.infrastructure.http;

import fakezin.dev.budgeting.application.ListTransactionsByCategoryUseCase;
import fakezin.dev.budgeting.application.PersistTransactionUseCase;
import fakezin.dev.budgeting.domain.Category;
import fakezin.dev.budgeting.infrastructure.http.request.TransactionRequest;
import fakezin.dev.budgeting.infrastructure.http.response.TransactionResponse;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final PersistTransactionUseCase persistTransactionUseCase;
    private final ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase;

    private final OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;
    private final ChatClient  chatClient;

    private final OpenAiAudioSpeechModel openAiAudioSpeechModel;

    public TransactionController(PersistTransactionUseCase persistTransactionUseCase,
                                 ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase,
                                 OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel,
                                 @Value("classpath:/prompts/system-message.st") Resource systemPrompt,
                                 ChatClient.Builder chatClientBuilder,
                                 OpenAiAudioSpeechModel openAiAudioSpeechModel) throws IOException {
        this.persistTransactionUseCase = persistTransactionUseCase;
        this.listTransactionsByCategoryUseCase = listTransactionsByCategoryUseCase;
        this.openAiAudioTranscriptionModel = openAiAudioTranscriptionModel;
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .defaultTools(persistTransactionUseCase, listTransactionsByCategoryUseCase)
                .build();
        this.openAiAudioSpeechModel = openAiAudioSpeechModel;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody TransactionRequest request) {
        var transaction = persistTransactionUseCase.execute(request.toInput());
        return TransactionResponse.from(transaction);
    }

    @GetMapping("/{category}")
    public List<TransactionResponse> readTransactions(@PathVariable Category category) {
        return listTransactionsByCategoryUseCase.execute(category).stream().map(TransactionResponse::from).toList();
    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mp3")
    ResponseEntity<byte[]> transcribe(@RequestParam("file") MultipartFile file) {
        var resource = file.getResource();
        var response = openAiAudioTranscriptionModel.call(new AudioTranscriptionPrompt(resource));
        var content = response.getResult().getOutput();

        var result = chatClient.prompt().user(content).call().content();

        byte[] audioBytes = openAiAudioSpeechModel.call(result);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"speech.mp3\"")
                .contentType(MediaType.valueOf("audio/mpeg"))
                .body(audioBytes);

    }
}
