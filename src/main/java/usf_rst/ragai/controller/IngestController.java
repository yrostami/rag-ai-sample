package usf_rst.ragai.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usf_rst.ragai.service.DocumentIngestionService;

@Slf4j
@RestController
public class IngestController {

	private final DocumentIngestionService ingestionService;

	public IngestController(DocumentIngestionService ingestionService) {
		this.ingestionService = ingestionService;
	}

	@PostMapping("/ingest")
	public String ingest(@RequestParam(defaultValue = "false") boolean force) {
		log.info("ingestion triggered, force={}", force);
		int chunks = ingestionService.ingestAll(force);
		return "ingested %d new/changed chunk(s)".formatted(chunks);
	}
}
