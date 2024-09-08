package com.example.storage.controller;

import com.example.storage.dto.FileDto;
import com.example.storage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/file")
public class FileController {

    private final FileService fileService;

    @Autowired
    FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileDto> getFile(@PathVariable("id") UUID id) {
        Optional<FileDto> file = fileService.getFileById(id);
        return file.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<Page<FileDto>> getFiles(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        Page<FileDto> files= fileService.getAll(page, size);
        if (files.getContent().isEmpty())
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok(files);
    }

    @PostMapping
    public ResponseEntity<UUID> createFile(@RequestBody FileDto fileDto) {
        return ResponseEntity.ok(fileService.createFile(fileDto));
    }

}
