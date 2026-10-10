package usf_rst.ragai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.session.DefaultSessionService;
import org.springframework.ai.session.InMemorySessionRepository;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.ai.session.compaction.SlidingWindowCompactionStrategy;
import org.springframework.ai.session.compaction.TurnCountTrigger;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import usf_rst.ragai.service.rerank.CohereRerankModel;
import usf_rst.ragai.service.rerank.LlmRerankModel;
import usf_rst.ragai.service.rerank.RerankModel;
import usf_rst.ragai.service.rerank.RerankingVectorStore;

@Configuration
public class AiConfig {

	@Bean
	SessionService sessionService() {
		return DefaultSessionService.builder()
				.sessionRepository(InMemorySessionRepository.builder().build())
				.build();
	}

	@Bean
	SessionMemoryAdvisor sessionMemoryAdvisor(SessionService sessionService) {
		return SessionMemoryAdvisor.builder(sessionService)
				.compactionTrigger(new TurnCountTrigger(20))
				.compactionStrategy(SlidingWindowCompactionStrategy.builder()
						.maxEvents(10)
						.build())
				.build();
	}

	@Bean
	RerankModel rerankModel(ChatClient.Builder chatClientBuilder,
			@Value("${ragai.rerank.provider:llm}") String provider,
			@Value("${ragai.rerank.cohere.base-url:https://api.avalai.ir/v1}") String cohereBaseUrl,
			@Value("${ragai.rerank.cohere.model:cohere-rerank-v4.0-pro}") String cohereModel,
			@Value("${ragai.rerank.cohere.max-doc-chars:2000}") int cohereMaxDocChars,
			@Value("${ragai.rerank.cohere.api-key:${spring.ai.openai.api-key:}}") String cohereApiKey) {
		if ("cohere".equalsIgnoreCase(provider)) {
			return new CohereRerankModel(cohereBaseUrl, cohereApiKey, cohereModel, cohereMaxDocChars);
		}
		return new LlmRerankModel(chatClientBuilder);
	}

	@Bean
	QuestionAnswerAdvisor QAAdvisor(VectorStore vectorStore, RerankModel rerankModel,
			@Value("${ragai.rerank.enabled:true}") boolean rerankEnabled,
			@Value("${ragai.rerank.candidate-multiplier:4}") int candidateMultiplier,
			@Value("${ragai.rerank.max-candidates:16}") int maxCandidates) {
		VectorStore store = rerankEnabled
				? new RerankingVectorStore(vectorStore, rerankModel, candidateMultiplier, maxCandidates)
				: vectorStore;
		return QuestionAnswerAdvisor.builder(store).build();
	}

	@Bean
	ChatClient chatClient(ChatClient.Builder builder, SessionMemoryAdvisor memoryAdvisor, QuestionAnswerAdvisor QAAdvisor) {
		return builder
				.defaultAdvisors(QAAdvisor, memoryAdvisor)
				.build();
	}
}
