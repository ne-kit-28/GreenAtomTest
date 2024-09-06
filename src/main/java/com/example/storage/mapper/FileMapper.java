package com.example.storage.mapper;

import com.example.storage.domain.File;
import com.example.storage.dto.FileDto;
import org.mapstruct.Mapper;

import java.util.ArrayList;

@Mapper
public interface FileMapper {
    FileDto fileToFileDto(File file);
    File fileDtoToFile(FileDto fileDto);

    ArrayList<FileDto> filesToFilesDto(ArrayList<File> files);
}
