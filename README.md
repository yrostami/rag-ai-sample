# ragai — Sample RAG AI Project

> **This is a sample project demonstrating a simple RAG (Retrieval-Augmented Generation) AI application.**
> It is intentionally minimal: it reads a small fictional documentation corpus, embeds it into a vector store, and answers questions grounded in those documents — with conversation memory and a tiny chat UI.

## What it does

1. **Ingestion** — On startup, all Markdown/CSV/TXT files under `rag-docs/acme-cloud/` (a fictional SaaS product called *Acme Cloud*) are read, split into ~800-token chunks, embedded, and stored in a **Chroma** vector database.
2. **Chat** — `GET /chat` retrieves the most relevant chunks for a question via a `QuestionAnswerAdvisor`, adds **conversation memory** (in-memory, keyed by a session ID), and returns the answer plus the **source file names** used.
3. **UI** — A dependency-free static chat page served at `http://localhost:8080/`.

## Tech stack

| Component | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.1.1 |
| AI | Spring AI 2.0.1 (OpenAI-compatible API) |
| Vector store | Chroma (`spring-ai-starter-vector-store-chroma`) |
| Session memory | `spring-ai-session-management` 0.5.0 (in-memory) |
| Misc | Lombok, vanilla HTML/CSS/JS frontend |

## Prerequisites

- **Java 25** and Maven (use the bundled wrapper: `mvnw` / `mvnw.cmd`)
- **Chroma** running locally, e.g. via Docker:

  ```bash
  docker run -p 8000:8000 chromadb/chroma
  ```

- An **API key** for an OpenAI-compatible endpoint, set in `src/main/resources/application.properties` (`spring.ai.openai.api-key`). Do not commit real keys.

## Configuration

`src/main/resources/application.properties`:

```properties
# Docs to ingest
ragai.docs.location=rag-docs/acme-cloud
ragai.ingest.on-startup=true
ragai.ingest.state-file=.ingest-state.properties

# OpenAI-compatible chat + embedding models
spring.ai.openai.base-url=https://api.avalai.ir/v1
spring.ai.openai.api-key=<YOUR-KEY>
spring.ai.openai.chat.model=glm-5.3-flash
spring.ai.openai.embedding.model=text-embedding-ada-002

# Chroma
spring.ai.vectorstore.chroma.client.host=http://localhost
spring.ai.vectorstore.chroma.client.port=8000
spring.ai.vectorstore.chroma.initialize-schema=true
spring.ai.vectorstore.chroma.tenant-name=ragai-tenant
spring.ai.vectorstore.chroma.database-name=ragai-db
spring.ai.vectorstore.chroma.collection-name=ragai-collection
```

## Running

```bash
./mvnw spring-boot:run        # Windows: .\mvnw.cmd spring-boot:run
```

Then open **http://localhost:8080/** — or call the API directly:

```bash
# Ask a question (a session ID is generated and returned if you omit sid)
curl "http://localhost:8080/chat?q=Which%20plan%20has%20SCIM"

# Continue the same conversation
curl "http://localhost:8080/chat?sid=<sessionId>&q=Why%3F"

# Force re-ingestion of all docs
curl -X POST "http://localhost:8080/ingest?force=true"
```

### Response format

```json
{
  "answer": "SCIM is available on the Enterprise plan only...",
  "sources": ["16-sso-setup.md", "04-pricing.md"],
  "sessionId": "c8b48e79-e2cb-4072-b4a2-723640df77d4"
}
```

## How ingestion stays idempotent

Calling ingestion repeatedly never duplicates vectors:

- **Hash state file** (`.ingest-state.properties`) records each file's MD5; unchanged files are skipped entirely (no re-embedding on restarts).
- **Deterministic chunk IDs** derived from `source + chunk index + content hash` — Chroma upserts by ID.
- **Delete-by-source** before adding, so changed files leave no stale chunks.

## Reranking (optional)

Retrieval fetches `topK × candidate-multiplier` documents (default 16) and re-scores them before the top `topK` are given to the model. Toggle with `ragai.rerank.enabled`. Two backends, selected by `ragai.rerank.provider`:

| Provider | How it works | Score range | Cost |
|---|---|---|---|
| `cohere` (default) | AvalAI `POST /v1/rerank` with `cohere-rerank-v4.0-pro` (also: `cohere-rerank-v4.0-fast`, `cohere.rerank-v3-5:0`, `qwen3-rerank`) | 0–1 | ~$0.0025/query |
| `llm` | The chat model scores each candidate 0–10 via structured output | 0–10 | Token cost |

The Cohere backend reuses the same API key as the chat model (`spring.ai.openai.api-key`) unless `ragai.rerank.cohere.api-key` is set. If the reranker fails for any reason, the original similarity order is used and RAG keeps working.

## Project structure

```
src/main/java/usf_rst/ragai/
├── RagaiApplication.java            # Entry point
├── config/AiConfig.java             # ChatClient, QA advisor, session memory beans
├── controller/RagController.java    # GET /chat (answer + sources + sessionId)
├── controller/ChatAnswer.java       # Response record
├── controller/IngestController.java # POST /ingest
├── service/DocumentIngestionService.java  # read → chunk → embed → store
└── service/rerank/                  # LLM-as-reranker (RerankModel, LlmRerankModel, RerankingVectorStore)

src/main/resources/static/          # Chat UI (index.html, style.css, app.js)
docs/acme-cloud/                    # Fictional knowledge base (21 files)
```

## Notes for tinkering

- Delete `.ingest-state.properties` (or `POST /ingest?force=true`) to re-embed everything.
- Session memory is in-memory only — restarting the app clears conversations; the UI picks up the new state automatically via `sessionId`.
- After renaming or moving Java classes, run `mvnw clean compile` — stale `.class` files in `target/` can cause phantom duplicate beans.
- The `rag-docs/acme-cloud/` content is entirely fictional and exists to give the RAG pipeline something to retrieve.

---

*This repository is a sample/learning project for building a simple RAG AI application — feel free to fork it and swap in your own documents, models, and vector store.*
