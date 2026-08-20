package fakezin.dev.budgeting;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.net.URI;

@Configuration
public class GeminiUrlConfig {

    @Bean
    public RestClientCustomizer geminiRestClientCustomizer() {
        return restClientBuilder -> restClientBuilder.requestInterceptor((request, body, execution) -> {
            URI uri = request.getURI();
            // Remove o /v1 duplicado exigido pela Google apenas para URLs do Google Gemini
            if (uri.getHost() != null && uri.getHost().contains("googleapis.com") && uri.toString().contains("/openai/v1/")) {
                URI adjustedUri = URI.create(uri.toString().replace("/openai/v1/", "/openai/"));
                return execution.execute(new HttpRequestWrapper(request) {
                    @Override
                    public URI getURI() {
                        return adjustedUri;
                    }
                }, body);
            }
            return execution.execute(request, body);
        });
    }
}