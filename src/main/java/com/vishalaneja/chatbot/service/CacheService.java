package com.vishalaneja.chatbot.service;

public interface CacheService {

	 public String getFromCache(String prompt);
	 
	 public void saveToCache(String prompt, String response);
}
