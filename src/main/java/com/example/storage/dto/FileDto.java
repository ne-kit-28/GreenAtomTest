package com.example.storage.dto;

import java.time.LocalDateTime;

public record FileDto(String title, String description, LocalDateTime creation_date, String base64FileData) {
}
