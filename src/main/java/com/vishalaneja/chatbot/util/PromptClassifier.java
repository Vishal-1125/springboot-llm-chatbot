package com.vishalaneja.chatbot.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PromptClassifier {
	@Value("${chatbot.prompt.cache-exclude-keywords}")
	private List<String> dynamicKeywords;
	  
	  public boolean isCacheable(String prompt) {
	        String lower = prompt.trim().toLowerCase();
	        return dynamicKeywords.stream().noneMatch(word->lower.contains(word));
	    }
}
