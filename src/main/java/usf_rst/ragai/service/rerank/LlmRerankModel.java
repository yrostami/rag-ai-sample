package usf_rst.ragai.service.rerank;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Comparator;
import java.util.List;

@Slf4j
public class LlmRerankModel implements RerankModel {

	private static final int SNIPPET_LENGTH = 800;

	private final ChatClient chatClient;

	public LlmRerankModel(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}

	public record ScoredIndex(int index, double score) {
	}

	@Override
	public List<Document> rerank(String query, List<Document> candidates) {
		if (candidates.size() < 2) {
			return candidates;
		}
		try {
			List<ScoredIndex> scored = chatClient.prompt().user(rerankPrompt(query, candidates))
					.call()
					.entity(new ParameterizedTypeReference<List<ScoredIndex>>() {
					});

			if (scored == null || scored.isEmpty()) {
				throw new IllegalStateException("reranker returned no scores");
			}

			return scored.stream()
					.filter(s -> s.index() >= 0 && s.index() < candidates.size())
					.sorted(Comparator.comparingDouble(ScoredIndex::score).reversed())
					.map(s -> {
						Document doc = candidates.get(s.index());
						doc.getMetadata().put("rerank_score", s.score());
						return doc;
					})
					.toList();
		} catch (Exception e) {
			log.warn("reranking failed, keeping original order: {}", e.getMessage());
			return candidates;
		}
	}

	private String rerankPrompt(String query, List<Document> candidates) {
		StringBuilder docs = new StringBuilder();
		for (int i = 0; i < candidates.size(); i++) {
			String text = candidates.get(i).getText();
			docs.append("[").append(i).append("] ")
					.append(text, 0, Math.min(SNIPPET_LENGTH, text.length()))
					.append("\n\n");
		}
		return """
				Score how relevant each numbered document is to the query on a scale from 0 (irrelevant) to 10 (highly relevant).
				Judge relevance by whether the document helps answer the query.

				Query: %s

				Documents:
				%s""".formatted(query, docs);
	}
}
