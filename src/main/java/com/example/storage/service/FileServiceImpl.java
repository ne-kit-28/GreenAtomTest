package com.example.storage.service;

import com.example.storage.domain.File;
import com.example.storage.dto.FileDto;
import com.example.storage.mapper.FileMapper;
import com.example.storage.mapper.FileMapperImpl;
import com.example.storage.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService{

    private final FileRepository fileRepository;
    private final FileMapper fileMapper;


    @Autowired
    FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.fileMapper = new FileMapperImpl();
    }

    @Override
    public Page<FileDto> getAll(int page, int size) {
        long count = fileRepository.count();
        int numOfPages = count % size == 0 ? (int)(count/size) : (int)(count/size + 1);
        Pageable pageable = PageRequest.of(Math.min(page, numOfPages - 1), size, Sort.by(Sort.Order.asc("creationDate")));
        Page<File> filePage = fileRepository.findAll(pageable);

        List<FileDto> filesDto = filePage.stream()
                .map(fileMapper::fileToFileDto)
                .toList();

        return new PageImpl<>(filesDto, pageable, filePage.getTotalElements());
    }

    @Override
    public Optional<FileDto> getFileById(UUID id) {
        if (fileRepository.findById(id).isPresent())
            return Optional.ofNullable(fileMapper.fileToFileDto(fileRepository.findById(id).get()));
        else return Optional.empty();
    }

    @Override
    public UUID createFile(FileDto fileDto) {
        File file = fileMapper.fileDtoToFile(fileDto);
        File savedFile = fileRepository.save(file);
        return savedFile.getId();
    }
}
