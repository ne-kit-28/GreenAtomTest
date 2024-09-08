package com.example.storage.service;

import com.example.storage.domain.File;
import com.example.storage.dto.FileDto;
import com.example.storage.mapper.FileMapper;
import com.example.storage.mapper.FileMapperImpl;
import com.example.storage.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

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
    public Page<FileDto> getAll(int page, int size) { // получение определенной страницы
                                                      // с заданным количеством файлов
        if (size == 0) size = 10;//Валидация вводимых данных: проверка деления на 0
                                 // и ввод страницы без данных
        long count = fileRepository.count();
        if (count != 0) {
            int numOfPages = count % size == 0 ? (int) (count / size) : (int) (count / size + 1);
            page = Math.min(page, numOfPages - 1);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Order.asc("creationDate"))); // сортировка по возрастанию по дате создания
        Page<File> filePage = fileRepository.findAll(pageable);

        List<FileDto> filesDto = filePage.stream()
                .map(fileMapper::fileToFileDto)
                .toList();

        return new PageImpl<>(filesDto, pageable, filePage.getTotalElements());
    }

    @Override
    public Optional<FileDto> getFileById(UUID id) { // получение файла по id
        if (fileRepository.findById(id).isPresent())
            return Optional.ofNullable(fileMapper.fileToFileDto(fileRepository.findById(id).get()));
        else return Optional.empty();
    }

    @Override
    public UUID createFile(FileDto fileDto) { // создание файла
        File file = fileMapper.fileDtoToFile(fileDto);
        File savedFile = fileRepository.save(file);
        return savedFile.getId();
    }
}
