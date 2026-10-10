package usf_rst.ragai.controller;

import java.util.List;
import java.util.UUID;

public record ChatAnswer(String answer, List<String> sources, UUID sessionId) {
}
