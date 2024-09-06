package com.example.storage.service;

import com.example.storage.domain.File;
import com.example.storage.dto.FileDto;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface FileService {
    Optional<ArrayList<FileDto>> getAll();

    Optional<FileDto> getFileById(UUID id);

    UUID createFile(FileDto file);
}
