package com.example.storage.controller;

import com.example.storage.dto.FileDto;
import com.example.storage.repository.FileRepository;
import com.example.storage.service.FileService;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FileControllerTest {

    private final FileService fileService;
    private final FileRepository fileRepository;


    @LocalServerPort
    private Integer port;

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
    public LocalDateTime time = LocalDateTime.parse(LocalDateTime.now().format(formatter));// Из-за округления
    // миллисекунд тест может не проходить, поэтому уменшаем точность

    UUID idTest1;
    UUID idTest2;
    UUID idTest3;

    @Autowired
    FileControllerTest(FileService fileService, FileRepository fileRepository) {
        this.fileService = fileService;
        this.fileRepository = fileRepository;
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.useRelaxedHTTPSValidation();

        FileDto fileDto1 = FileDto.builder()
                .title("The first file")
                .base64FileData("aGVsbG8gd29ybGQ=")
                .creationDate(time)
                .description("Data: hello world")
                .build();

        FileDto fileDto2 = FileDto.builder()
                .title("The second file")
                .base64FileData("Z29vZCBtb3JuaW5n")
                .creationDate(time.plusHours(1))
                .description("Data: good morning")
                .build();

        FileDto fileDto3 = FileDto.builder()
                .title("The third file")
                .base64FileData("amF2YQ==")
                .creationDate(time.plusMinutes(10))
                .description("Data: java")
                .build();
        idTest1 = fileService.createFile(fileDto1);
        idTest2 = fileService.createFile(fileDto2);
        idTest3 = fileService.createFile(fileDto3); // заполнение бд перед кадым тестом
    }

    @AfterEach
    void tearDown() {
        fileRepository.deleteAll(); // отчистка бд после каждого теста
    }

    @Test
    void getFileTest() { //тест получения файла по id
        Response r = given()
                .get("/api/file/" + idTest1)
                .then()
                .extract().response();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            FileDto fileDto = objectMapper.readValue(r.asString(), FileDto.class);
            assertAll(
                    () -> assertEquals("The first file", fileDto.title(),
                            "Неверное название"),
                    () -> assertEquals("Data: hello world", fileDto.description(), "Неверное описание"),
                    () -> assertEquals(time, fileDto.creationDate(),
                            "Неверное время"),
                    () -> assertEquals("aGVsbG8gd29ybGQ=", fileDto.base64FileData(),
                            "Неверные данные")
            );
        } catch (JsonProcessingException ex) {
            Assertions.fail("jackson is bad");
        }
    }

    @Test
    void getFilesTest() { //тест получения всех файлов
        Response r = given()
                .queryParam("page", 0)
                .queryParam("size", 5)
                .get("/api/file/all")
                .then()
                .extract().response();

        try {
            List<FileDto> files = new ArrayList<>();
            JsonFactory f = new JsonFactory();
            JsonParser jp = f.createJsonParser(r.asString());
            jp.setCodec(new ObjectMapper());
            JsonNode node = jp.readValueAsTree();

            JsonNode books = node.findValue("content");
            Iterator<JsonNode> it = books.elements();
            while(it.hasNext()){
                JsonNode temp = it.next();
                FileDto fileDto = FileDto.builder()
                        .title(temp.findValue("title").asText())
                        .description(temp.findValue("description").asText())
                        .creationDate(LocalDateTime.parse(temp.findValue("creationDate").asText()))
                        .base64FileData(temp.findValue("base64FileData").asText())
                        .build();
                files.add(fileDto);
            }

            int totalPages = node.findValue("totalPages").asInt();
            int totalElements = node.findValue("totalElements").asInt();
            int size = node.findValue("size").asInt();
            int number = node.findValue("number").asInt();

            assertAll(
                    () -> assertEquals(1, totalPages,
                            "Неверное количество страниц"),
                    () -> assertEquals(3, totalElements,
                            "Неверное количество файлов"),
                    () -> assertEquals(5, size,
                            "Неверный размер"),
                    () -> assertEquals(0, number,
                            "Неверный номер страницы"),
                    () -> assertEquals(time, files.getFirst().creationDate(),
                            "Неверный 1 элемент"),
                    () -> assertEquals(time.plusMinutes(10), files.get(1).creationDate(),
                            "Неверный 2 элемент"),
                    () -> assertEquals(time.plusHours(1), files.get(2).creationDate(),
                            "Неверный 3 элемент")
            );

        } catch (JsonProcessingException ex) {
            Assertions.fail(r.asString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createFileTest() { // тест создания файла и возвращения id

        String body = "{\n" +
                "  \"title\": \"The fourth file\",\n" +
                "  \"description\": \"Data: Greenatom\",\n" +
                "  \"creationDate\": \"2018-10-02T20:34:11\",\n" +
                "  \"base64FileData\": \"R3JlZW5hdG9t\"\n" +
                "}";

        Response r = given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/file")
                .then()
                .extract().response();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            FileDto fileDto = fileService.getFileById(objectMapper.readValue(r.asString(), UUID.class)).get();

            assertAll(
                    () -> assertEquals("The fourth file", fileDto.title(),
                            "Неверное название"),
                    () -> assertEquals("Data: Greenatom", fileDto.description(), "Неверное описание"),
                    () -> assertEquals(LocalDateTime.parse("2018-10-02T20:34:11"), fileDto.creationDate(),
                            "Неверное время"),
                    () -> assertEquals("R3JlZW5hdG9t", fileDto.base64FileData(),
                            "Неверные данные")
            );
        } catch (JsonProcessingException ex) {
            Assertions.fail(r.asString());
        }
    }
}