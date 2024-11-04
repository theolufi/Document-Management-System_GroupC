package com.example.demo.document.services;

import com.example.demo.document.dto.DocumentDTO;
import com.example.demo.document.dto.DocumentDTOMapper;
import com.example.demo.document.entity.Document;
import com.example.demo.document.repository.DocumentRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;
    private final DocumentDTOMapper documentDTOMapper;
    private final RabbitTemplate rabbitTemplate;

    private final String queueName = "documentQueue";

    public DocumentDTO uploadDocument(String title, String content) {
        Document document = Document.builder()
                .title(title)
                .content(content)
                .build();

        Document savedDocument = documentRepository.save(document);
        DocumentDTO documentDTO = documentDTOMapper.toDTO(savedDocument);

        try {
            String message = "Document uploaded: " + savedDocument.getTitle();
            rabbitTemplate.convertAndSend(queueName, message);
            logger.info("Message sent to RabbitMQ: {}", message);
        } catch (Exception e) {
            logger.error("Failed to send message to RabbitMQ: {}", e.getMessage());
        }

        return documentDTO;
    }

    public String deleteDocument(Long id) {
        try {
            Document document = documentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Document not found"));
            documentRepository.deleteById(id);
            return "Document deleted successfully";
        } catch (Exception e) {
            logger.error("Error deleting document: {}", e.getMessage());
            return "Document not found";
        }
    }
}
