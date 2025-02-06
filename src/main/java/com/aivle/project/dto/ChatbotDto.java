package com.aivle.project.dto;

import lombok.*;


public class ChatbotDto {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class RequestData{
        private String response;
    }
}
