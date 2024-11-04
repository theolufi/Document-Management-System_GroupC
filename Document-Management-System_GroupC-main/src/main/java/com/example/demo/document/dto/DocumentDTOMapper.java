package com.example.demo.document.dto;

import com.example.demo.document.entity.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentDTOMapper {

    // Convert Document entity to DocumentDTO
    public static DocumentDTO toDTO(Document document) {
        if (document == null) {
            return null; 
        }
        return DocumentDTO.builder()
                .id(document.getId())
                .title(document.getTitle())
                .content(document.getContent())
                .build();
    }

    // Convert DocumentDTO to Document entity
    public Document toEntity(DocumentDTO documentDTO) {
        if (documentDTO == null) {
            return null; 
        }
        return Document.builder()
                .id(documentDTO.getId())
                .title(documentDTO.getTitle())
                .content(documentDTO.getContent())
                .build();
    }
}
