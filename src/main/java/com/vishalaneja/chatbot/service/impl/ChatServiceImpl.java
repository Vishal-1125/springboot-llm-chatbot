package com.vishalaneja.chatbot.service.impl;

import java.util.concurrent.CompletableFuture;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vishalaneja.chatbot.dto.request.ChatRequest;
import com.vishalaneja.chatbot.dto.response.ResponseData;
import com.vishalaneja.chatbot.service.CacheService;
import com.vishalaneja.chatbot.service.ChatService;
import com.vishalaneja.chatbot.util.PromptClassifier;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

	private final ChatClient chatClient;

	private final PromptClassifier promptClassifier;

	private final CacheService cacheService;

	@Autowired
	public ChatServiceImpl(ChatClient chatClient, PromptClassifier promptClassifier, CacheService cacheService) {
		this.chatClient = chatClient;
		this.promptClassifier = promptClassifier;
		this.cacheService = cacheService;

	}

	@Override
	@CircuitBreaker(name = "chatServiceCircuitBreaker", fallbackMethod = "chatResponseFallback")
	@Retry(name = "chatServiceRetry", fallbackMethod = "chatResponseFallback")
	@RateLimiter(name = "chatServiceRateLimiter", fallbackMethod = "chatResponseFallback")
	@TimeLimiter(name = "chatServiceTimeLimiter", fallbackMethod = "chatResponseFallback")
	public CompletableFuture<ResponseEntity<ResponseData<String>>> chatResponseService(ChatRequest chatRequest) {
		return CompletableFuture.supplyAsync(() -> {
			log.info("Processing LLM request: {}", chatRequest);

			String prompt = chatRequest.getMessage();
			log.info("Incoming prompt: {}", prompt);

			boolean cacheable = promptClassifier.isCacheable(prompt);

			if (cacheable) {
				String cachedResponse = cacheService.getFromCache(prompt);
				if (cachedResponse != null) {
					log.info("Returning cached response for prompt: {}", prompt);
					return ResponseEntity.ok(new ResponseData<>(cachedResponse, "Response from cache", "0"));
				}
			}
			String llmResponse = chatClient.prompt(prompt).call().content();
			log.info("Generated LLM response: {}", llmResponse);

			if (cacheable) {
				cacheService.saveToCache(prompt, llmResponse);
			}

			return ResponseEntity.ok(new ResponseData<>(llmResponse, "LLM response generated successfully", "0"));
		});
	}

	public CompletableFuture<ResponseEntity<ResponseData<String>>> chatResponseFallback(ChatRequest chatRequest,
			Throwable throwable) {
		log.warn("Fallback triggered for chatResponseService due to: {}", throwable.getMessage());

		ResponseData<String> fallbackResponse = new ResponseData<>();
		fallbackResponse.setData("Service is currently unavailable. Please try again later.");
		fallbackResponse.setMessage("Fallback response due to service failure");
		fallbackResponse.setStatus("1");

		return CompletableFuture
				.completedFuture(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse));
	}

}
