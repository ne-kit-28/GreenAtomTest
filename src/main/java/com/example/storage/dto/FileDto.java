package com.example.storage.dto;

import java.time.LocalDateTime;

public record FileDto(String title, String description, LocalDateTime creationDate, String base64FileData) {
}
