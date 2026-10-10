package usf_rst.ragai.service.rerank;

import org.springframework.ai.document.Document;

import java.util.List;

public interface RerankModel {

	List<Document> rerank(String query, List<Document> candidates);
}
