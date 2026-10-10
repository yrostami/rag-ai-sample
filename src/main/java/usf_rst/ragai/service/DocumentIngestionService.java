package usf_rst.ragai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
public class DocumentIngestionService implements ApplicationRunner {

	private static final List<String> SUPPORTED_EXTENSIONS = List.of(".md", ".csv", ".txt");

	private final VectorStore vectorStore;
	private final String docsLocation;
	private final Path stateFile;
	private final boolean ingestOnStartup;

	public DocumentIngestionService(VectorStore vectorStore,
			@Value("${ragai.docs.location:docs/acme-cloud}") String docsLocation,
			@Value("${ragai.ingest.state-file:.ingest-state.properties}") String stateFile,
			@Value("${ragai.ingest.on-startup:true}") boolean ingestOnStartup) {
		this.vectorStore = vectorStore;
		this.docsLocation = docsLocation;
		this.stateFile = Path.of(stateFile);
		this.ingestOnStartup = ingestOnStartup;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (ingestOnStartup) {
			ingestAll();
		}
	}

	public int ingestAll() {
		return ingestAll(false);
	}

	public int ingestAll(boolean force) {
		List<Document> files = readDocuments();
		log.info("read {} document(s) from {}", files.size(), docsLocation);

		Properties state = loadState();

		int stored = 0;
		for (Document file : files) {
			String source = (String) file.getMetadata().get("source");
			String hash = (String) file.getMetadata().get("hash");
			if (!force && hash.equals(state.getProperty(source))) {
				log.info("skipping unchanged file: {}", source);
				continue;
			}
			stored += ingestFile(source, file);
			state.setProperty(source, hash);
		}

		saveState(state);
		log.info("ingestion finished, stored {} new/changed chunk(s)", stored);
		return stored;
	}

	private int ingestFile(String source, Document file) {
		List<Document> chunks = TokenTextSplitter.builder().build().apply(List.of(file));
		List<Document> idempotent = withDeterministicIds(source, chunks);
		vectorStore.delete(new FilterExpressionBuilder().eq("source", source).build());
		vectorStore.add(idempotent);
		log.info("stored {} chunk(s) for {}", idempotent.size(), source);
		return idempotent.size();
	}

	private List<Document> readDocuments() {
		try (Stream<Path> paths = Files.walk(Path.of(docsLocation))) {
			return paths.filter(Files::isRegularFile)
					.filter(this::isSupported)
					.map(this::toDocument)
					.filter(Objects::nonNull)
					.toList();
		} catch (IOException e) {
			throw new IllegalStateException("failed to read documents from " + docsLocation, e);
		}
	}

	private boolean isSupported(Path path) {
		String name = path.getFileName().toString().toLowerCase();
		return SUPPORTED_EXTENSIONS.stream().anyMatch(name::endsWith);
	}

	private Document toDocument(Path path) {
		try {
			String content = Files.readString(path);
			if (content.isBlank()) {
				log.warn("skipping empty file: {}", path);
				return null;
			}
			return new Document(content, Map.of(
					"source", path.toString(),
					"file", path.getFileName().toString(),
					"hash", DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8))));
		} catch (IOException e) {
			throw new IllegalStateException("failed to read " + path, e);
		}
	}

	private List<Document> withDeterministicIds(String source, List<Document> chunks) {
		List<Document> result = new ArrayList<>(chunks.size());
		for (int i = 0; i < chunks.size(); i++) {
			Document chunk = chunks.get(i);
			String id = UUID.nameUUIDFromBytes((source + "#" + i + "#" + chunk.getMetadata().get("hash"))
					.getBytes(StandardCharsets.UTF_8)).toString();
			result.add(new Document(id, chunk.getText(), chunk.getMetadata()));
		}
		return result;
	}

	private Properties loadState() {
		Properties state = new Properties();
		if (Files.exists(stateFile)) {
			try (InputStream in = Files.newInputStream(stateFile)) {
				state.load(in);
			} catch (IOException e) {
				log.warn("could not read ingest state file: {}", stateFile, e);
			}
		}
		return state;
	}

	private void saveState(Properties state) {
		try (OutputStream out = Files.newOutputStream(stateFile)) {
			state.store(out, "ingested document hashes");
		} catch (IOException e) {
			log.warn("could not write ingest state file: {}", stateFile, e);
		}
	}
}
