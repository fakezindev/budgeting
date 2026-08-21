# 🎙️ Budgeting AI - Assistente Inteligente de Controle Financeiro

Uma API inteligente desenvolvida em **Java** e **Spring Boot** integrada ao **Spring AI** e **OpenAI**. A aplicação permite que o usuário gerencie suas despesas financeiras através de comandos de voz, realizando a transcrição do áudio, interpretação do gasto com inferência de categoria via inteligência artificial, execução automática de funções no banco de dados (*Function Calling / Tools*) e resposta falada em áudio (*Text-to-Speech*).

---

## 📌 O que o projeto faz

O **Budgeting AI** automatiza o registro e a consulta de gastos cotidianos com um fluxo conversacional multimodal ponta a ponta:

1. **Recepção do Áudio**: O usuário envia um arquivo de áudio relatando uma despesa (ex: *"Gastei 50 reais na farmácia comprando remédios"* ou *"Quanto gastei com alimentação?"*).
2. **Transcrição (Speech-to-Text)**: O áudio é transcrito em texto utilizando o modelo **OpenAI Whisper** (`whisper-1`).
3. **Interpretação e Execução de Ferramentas (LLM + Function Calling)**: O modelo **GPT-4o-mini** analisa o texto, extrai os valores, infere a categoria adequada e aciona automaticamente os casos de uso da aplicação anotados com `@Tool` (`PersistTransactionUseCase` e `ListTransactionsByCategoryUseCase`) para interagir com o banco de dados MySQL.
4. **Resposta em Áudio (Text-to-Speech)**: A confirmação ou resposta gerada pela IA é convertida em fala pelo modelo **OpenAI TTS** (`tts-1`) com voz natural em português e devolvida em formato MP3.
5. **Endpoints REST Tradicionais**: Além do fluxo com IA, disponibiliza endpoints para cadastro e consulta direta de transações via JSON.

---

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.3.3**
- **Spring AI (1.0.0-M6)**:
  - `OpenAiAudioTranscriptionModel` (Whisper)
  - `ChatClient` com suporte nativo a `@Tool` (Function Calling)
  - `OpenAiAudioSpeechModel` (TTS)
- **Spring Data JPA & Hibernate**
- **MySQL 9.6** (via Docker Compose)
- **Lombok**
- **Gradle**

---

## 🚀 Como Executar a Aplicação

### 1. Pré-requisitos
- **JDK 21** instalado
- **Docker** e **Docker Compose** em execução
- Chave de API da OpenAI (`OPENAI_API_KEY`)

### 2. Clonar o repositório
```bash
git clone <url-do-repositorio>
cd budgeting
```

### 3. Iniciar o Banco de Dados MySQL (Docker)
Suba o container do MySQL configurado no `compose.yml`:
```bash
docker compose up -d
```

### 4. Definir a Chave da OpenAI
Defina a variável de ambiente no seu terminal:

- **Windows (PowerShell):**
  ```powershell
  $env:OPENAI_API_KEY="sk-proj-sua-chave-aqui"
  ```
- **Windows (CMD):**
  ```cmd
  set OPENAI_API_KEY=sk-proj-sua-chave-aqui
  ```
- **Linux / macOS:**
  ```bash
  export OPENAI_API_KEY="sk-proj-sua-chave-aqui"
  ```

### 5. Executar a Aplicação
```bash
./gradlew bootRun
```
A API estará disponível em: `http://localhost:8080`

---

## 💡 Melhorias Implementadas

- **Pipeline Multimodal Completo (Áudio ➡️ Ação ➡️ Áudio)**: Implementação do endpoint `/transactions/ai` que recebe áudio via `multipart/form-data`, transcreve, processa a lógica de negócio e retorna diretamente um arquivo de áudio `audio/mpeg` para reprodução.
- **Integração com Spring AI Tools (Function Calling)**: Os casos de uso de persistência e listagem foram desacoplados e anotados com `@Tool`, permitindo que a IA invoque métodos Java diretamente no fluxo de execução.
- **Categorização Inteligente de Gastos**: Prompt de sistema otimizado em arquivo de recurso (`system-message.st`) para classificar automaticamente transações nas categorias corretas (`ALIMENTACAO`, `SAUDE`, `TRANSPORTE`, `LAZER`, `OUTROS`).
- **Arquitetura Limpa e Organizada**: Separação clara de responsabilidades entre Domínio, Casos de Uso (Aplicação), Infraestrutura HTTP/Persistência e Recursos de Prompts.
- **Segurança e Proteção de Dados**: Configuração detalhada do `.gitignore` para blindar chaves de API, arquivos de ambiente, logs e artefatos de compilação.

---

## 🧪 Como Testar o Fluxo Principal

### 1. Fluxo Principal com IA (Envio de Áudio e Retorno de Áudio)
Envie um arquivo de áudio com a gravação de um gasto:

```bash
curl -X POST "http://localhost:8080/transactions/ai" \
  -H "Accept: audio/mp3" \
  -F "file=@src/test/resources/audio/recording-1.mp3" \
  --output resposta_assistente.mp3
```
> O arquivo `resposta_assistente.mp3` será gerado com a voz do assistente confirmando o registro da transação no banco de dados.

### 2. Consultar Transações por Categoria (REST)
```bash
curl -X GET "http://localhost:8080/transactions/ALIMENTACAO"
```

### 3. Cadastrar Transação Manualmente (REST)
```bash
curl -X POST "http://localhost:8080/transactions" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Almoço de domingo",
    "amount": 85.50,
    "category": "ALIMENTACAO"
  }'
```

### 4. Executar os Testes Automatizados
```bash
./gradlew test
```

---

## 🎓 O que Aprendemos Durante o Desafio

- **Integração com Spring AI**: Como configurar e utilizar modelos de chat, transcrição de áudio e síntese de voz de forma idiomática no ecossistema Spring.
- **Function Calling / Tool Calling na Prática**: Como capacitar um modelo de linguagem a tomar decisões e acionar código backend de forma autônoma e tipada usando `@Tool` e `@ToolParam`.
- **Processamento de Mídia em APIs REST**: Como receber `MultipartFile`, converter para `Resource` do Spring e retornar respostas binárias de áudio (`ResponseEntity<byte[]>`) com headers HTTP apropriados (`Content-Type`, `Content-Disposition`).
- **Externalização de Prompts**: Utilização de templates de prompt estruturados (`.st`) desacoplados do código Java, facilitando ajustes de comportamento da IA sem necessidade de recompilação da lógica central.
- **Construção de Aplicações Orientadas a IA Generativa**: Boas práticas de arquitetura em microsserviços modernos com IA integrada ao fluxo de negócios.
