package com.example.storage.service;

import com.example.storage.domain.File;
import com.example.storage.dto.FileDto;
import com.example.storage.mapper.FileMapper;
import com.example.storage.mapper.FileMapperImpl;
import com.example.storage.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public Optional<ArrayList<FileDto>> getAll() {
        ArrayList<File> filesDto = new ArrayList<>(fileRepository.findAll(
                Sort.by(Sort.Order.asc("creationDate"))));
        if (filesDto.isEmpty())
            return Optional.empty();
        else
            return Optional.of(new ArrayList<>(fileMapper.filesToFilesDto(filesDto)));
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
