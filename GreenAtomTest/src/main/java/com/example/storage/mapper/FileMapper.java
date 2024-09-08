package com.example.storage.mapper;

import com.example.storage.domain.File;
import com.example.storage.dto.FileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Base64;

import java.util.ArrayList;

@Mapper
public interface FileMapper {
    @Mapping(target = "base64FileData", source = "fileData", qualifiedByName = "fileDataToBase64")
    FileDto fileToFileDto(File file); //объект в DTO
    @Mapping(target = "fileData", source = "base64FileData", qualifiedByName = "base64ToFileData")
    File fileDtoToFile(FileDto fileDto); //DTO в объект


    @Named("fileDataToBase64")
    default String fileDataToBase64(byte[] fileData) {return Base64.getEncoder().encodeToString(fileData); }

    @Named("base64ToFileData")
    default byte[] base64ToFileData(String base64FileData) {return Base64.getDecoder().decode(base64FileData); }

    ArrayList<FileDto> filesToFilesDto(ArrayList<File> files);
}
