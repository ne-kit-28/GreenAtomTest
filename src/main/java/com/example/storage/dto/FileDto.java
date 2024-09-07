package com.example.storage.dto;

import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record FileDto(String title, String description, LocalDateTime creationDate, String base64FileData) {
}
