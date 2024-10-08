package com.example.storage.service;

import com.example.storage.dto.FileDto;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface FileService {

    Page<FileDto> getAll(int page, int size);

    Optional<FileDto> getFileById(UUID id);

    UUID createFile(FileDto file);
}
