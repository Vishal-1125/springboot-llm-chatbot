package com.vishalaneja.chatbot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor	
public class ResponseData<T> {

	private T data;
	private String message;
	private String status;
}
