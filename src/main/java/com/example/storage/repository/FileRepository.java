package com.example.storage.repository;

import com.example.storage.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {
    Optional<File> findById(UUID id);
}