package com.vishalaneja.chatbot.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vishalaneja.chatbot.dto.request.ChatRequest;
import com.vishalaneja.chatbot.dto.response.ResponseData;
import com.vishalaneja.chatbot.service.ChatService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ChatController {

	@Autowired
	ChatService chatService;

	@PostMapping("/chat")
	public CompletableFuture<ResponseEntity<ResponseData<String>>> chatResposneApi(
			@RequestBody ChatRequest chatRequest) {
		long startTime = System.currentTimeMillis();
		return chatService.chatResponseService(chatRequest).thenApply(response -> {
			long duration = System.currentTimeMillis() - startTime;
			log.info("LLM response completed in {} ms", duration);
			return response;
		});
	}

}
