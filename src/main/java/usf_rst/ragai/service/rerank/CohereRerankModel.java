package usf_rst.ragai.service.rerank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class CohereRerankModel implements RerankModel {

	private final RestClient restClient;
	private final String model;
	private final int maxDocChars;

	public CohereRerankModel(String baseUrl, String apiKey, String model, int maxDocChars) {
		HttpClient httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(10))
				.build();
		JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
		requestFactory.setReadTimeout(Duration.ofSeconds(60));
		this.restClient = RestClient.builder()
				.requestFactory(requestFactory)
				.baseUrl(baseUrl)
				.defaultHeader("Authorization", "Bearer " + apiKey)
				.build();
		this.model = model;
		this.maxDocChars = maxDocChars;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record RerankRequest(String model, String query, List<String> documents, Boolean return_documents) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record RerankResponse(List<RerankResult> results, Object id, Object meta) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record RerankResult(int index, double relevance_score) {
	}

	@Override
	public List<Document> rerank(String query, List<Document> candidates) {
		if (candidates.size() < 2) {
			return candidates;
		}
		try {
			List<String> texts = candidates.stream()
					.map(doc -> truncate(doc.getText()))
					.toList();
			RerankResponse response = restClient.post()
					.uri("/rerank")
					.body(new RerankRequest(model, query, texts, false))
					.retrieve()
					.body(RerankResponse.class);

			if (response == null || response.results() == null || response.results().isEmpty()) {
				throw new IllegalStateException("reranker returned no results");
			}

			return response.results().stream()
					.filter(r -> r.index() >= 0 && r.index() < candidates.size())
					.sorted(Comparator.comparingDouble(RerankResult::relevance_score).reversed())
					.map(r -> {
						Document doc = candidates.get(r.index());
						doc.getMetadata().put("rerank_score", r.relevance_score());
						return doc;
					})
					.toList();
		} catch (Exception e) {
			log.warn("cohere rerank failed, keeping original order: {}", e.getMessage());
			return candidates;
		}
	}

	private String truncate(String text) {
		if (text == null || text.length() <= maxDocChars) {
			return text;
		}
		return text.substring(0, maxDocChars);
	}
}
