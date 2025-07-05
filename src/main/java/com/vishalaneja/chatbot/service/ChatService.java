package com.vishalaneja.chatbot.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;

import com.vishalaneja.chatbot.dto.request.ChatRequest;
import com.vishalaneja.chatbot.dto.response.ResponseData;


public interface ChatService {
	public CompletableFuture<ResponseEntity<ResponseData<String>>> chatResponseService(ChatRequest chatRequest);
}
