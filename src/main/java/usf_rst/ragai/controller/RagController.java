package usf_rst.ragai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RagController {

	private final ChatClient chatClient;

	@GetMapping("/chat")
	public ChatAnswer chat(@RequestParam(name = "sid", required = false) UUID sessionId, @RequestParam(name = "q") String question) {

		log.info("asked: {}", question);
		UUID finalSessionId = sessionId != null ? sessionId : UUID.randomUUID();

		ChatResponse chatResponse = chatClient.prompt().user(question)
				.advisors(sessionIdAdvisor(finalSessionId))
				.call()
				.chatResponse();

		String answer = chatResponse != null && chatResponse.getResult() != null
				? chatResponse.getResult().getOutput().getText()
				: "no answer returned";
		List<String> sources = extractSources(chatResponse);

		log.info("response: {}", answer);
		return new ChatAnswer(answer, sources, finalSessionId);
	}

	private static List<String> extractSources(ChatResponse chatResponse) {
		if (chatResponse == null || chatResponse.getMetadata() == null) {
			return List.of();
		}
		List<Document> documents = chatResponse.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);
		if (documents == null) {
			return List.of();
		}
		return documents.stream()
				.map(doc -> (String) doc.getMetadata().get("file"))
				.filter(Objects::nonNull)
				.distinct()
				.toList();
	}

	private static Consumer<ChatClient.AdvisorSpec> sessionIdAdvisor(UUID finalSessionId) {
		return a -> a.param(SessionMemoryAdvisor.SESSION_ID_CONTEXT_KEY, finalSessionId.toString());
	}
}
