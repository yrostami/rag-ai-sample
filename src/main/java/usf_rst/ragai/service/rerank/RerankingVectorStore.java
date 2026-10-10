package usf_rst.ragai.service.rerank;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter.Expression;

import java.util.List;

@Slf4j
public class RerankingVectorStore implements VectorStore {

	private final VectorStore delegate;
	private final RerankModel rerankModel;
	private final int candidateMultiplier;
	private final int maxCandidates;

	public RerankingVectorStore(VectorStore delegate, RerankModel rerankModel,
			int candidateMultiplier, int maxCandidates) {
		this.delegate = delegate;
		this.rerankModel = rerankModel;
		this.candidateMultiplier = candidateMultiplier;
		this.maxCandidates = maxCandidates;
	}

	@Override
	public List<Document> similaritySearch(SearchRequest request) {
		int candidateCount = Math.min(request.getTopK() * candidateMultiplier, maxCandidates);
		SearchRequest wideRequest = SearchRequest.builder()
				.query(request.getQuery())
				.topK(candidateCount)
				.similarityThreshold(request.getSimilarityThreshold())
				.filterExpression(request.getFilterExpression())
				.build();

		List<Document> candidates = delegate.similaritySearch(wideRequest);
		if (candidates == null || candidates.size() <= request.getTopK()) {
			return candidates;
		}

		log.info("rerank input: query='{}', candidates={}", request.getQuery(), describe(candidates));
		List<Document> reranked = rerankModel.rerank(request.getQuery(), candidates);
		log.info("rerank output: {}", describe(reranked));

		return reranked.subList(0, Math.min(request.getTopK(), reranked.size()));
	}

	private static String describe(List<Document> docs) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < docs.size(); i++) {
			Document doc = docs.get(i);
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(i).append(":").append(doc.getMetadata().getOrDefault("file", doc.getId()));
			Object score = doc.getMetadata().get("rerank_score");
			if (score != null) {
				sb.append("(score=").append(score).append(")");
			}
		}
		return sb.append("]").toString();
	}

	@Override
	public void add(List<Document> documents) {
		delegate.add(documents);
	}

	@Override
	public void delete(List<String> idList) {
		delegate.delete(idList);
	}

	@Override
	public void delete(Expression filterExpression) {
		delegate.delete(filterExpression);
	}
}
