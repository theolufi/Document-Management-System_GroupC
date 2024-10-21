package com.example.demo.document.entity;

import org.antlr.v4.runtime.misc.NotNull;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@Entity
@Table(name = "documents")
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @SuppressWarnings("deprecation")
    @NotNull
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    @Column(name = "title")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 1000, message = "Content must be less than 1000 characters")
    @Column(name = "content")
    private String content;

    @SuppressWarnings("deprecation")
    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    @NotNull
    private byte[] fileData;

    @Builder // Lombok will generate a builder pattern for this entity
    public Document(Long id, String title, String content, byte[] fileData) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.fileData = fileData;
    }
}
