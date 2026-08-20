package fakezin.dev.budgeting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiChatModelIT {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private ChatClient chatClient;

    @Test
    void should_receiveResponse_when_chatModelIsCalled() {
        String prompt = "Gere um registro financeiro com: descrição do gasto, valor em reais e estabelecimento.";
        String response = chatModel.call(prompt);

        assertThat(response).isNotBlank();
        System.out.println("--- Resposta do OpenAI ChatModel ---");
        System.out.println(response);
    }

    @Test
    void should_receiveResponse_when_chatClientIsCalled() {
        String response = chatClient.prompt()
                .system("Você é um assistente financeiro especialista em categorização de gastos.")
                .user("Categorize o seguinte gasto: 'Almoço no restaurante por 45 reais'.")
                .call()
                .content();

        assertThat(response).isNotBlank();
        System.out.println("--- Resposta do OpenAI ChatClient ---");
        System.out.println(response);
    }
}
