package com.example.storage.controller;

import com.example.storage.domain.File;
import com.example.storage.dto.FileDto;
import com.example.storage.service.FileService;
import com.example.storage.service.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public ResponseEntity<FileDto> getMovie(@PathVariable("id") UUID id) {
        Optional<FileDto> file = fileService.getFileById(id);
        return file.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<ArrayList<FileDto>> getMovies() {
        if (fileService.getAll().isPresent())
            return ResponseEntity.ok(fileService.getAll().get());
        else return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<UUID> createFile(@RequestBody FileDto fileDto) {
        return ResponseEntity.ok(fileService.createFile(fileDto));
    }

}
