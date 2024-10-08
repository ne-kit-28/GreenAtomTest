package com.example.storage.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@FieldNameConstants
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "file")
public class File {

    @Id
    @UuidGenerator
    UUID id;


    @Column(nullable = false)
    private String title; //название

    @Column(nullable = false, length = 32)
    private String description; //описание документа

    @Column(nullable = false)
    private LocalDateTime creationDate; //дата и время отправки

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "fileData")
    private byte[] fileData; //данные файла
}
