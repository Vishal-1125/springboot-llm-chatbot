package com.vishalaneja.chatbot.service.impl;

import java.util.concurrent.CompletableFuture;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vishalaneja.chatbot.dto.request.ChatRequest;
import com.vishalaneja.chatbot.dto.response.ResponseData;
import com.vishalaneja.chatbot.service.ChatService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

	private final ChatClient chatClient;

	@Autowired
	public ChatServiceImpl(ChatClient chatClient) {
		this.chatClient = chatClient;

	}

	@Override
	@CircuitBreaker(name = "chatServiceCircuitBreaker", fallbackMethod = "chatResponseFallback")
	@Retry(name = "chatServiceRetry", fallbackMethod = "chatResponseFallback")
	@RateLimiter(name = "chatServiceRateLimiter", fallbackMethod = "chatResponseFallback")
	@TimeLimiter(name = "chatServiceTimeLimiter", fallbackMethod = "chatResponseFallback")
	public CompletableFuture<ResponseEntity<ResponseData<String>>> chatResponseService(ChatRequest chatRequest) {
		return CompletableFuture.supplyAsync(() -> {
			log.info("Processing LLM request: {}", chatRequest);
			String response = chatClient.prompt(chatRequest.getMessage()).call().content().trim();

			ResponseData<String> responseData = new ResponseData<>();
			responseData.setData(response);
			responseData.setMessage("Data fetched successfully");
			responseData.setStatus("0");

			return ResponseEntity.ok(responseData);
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
