package com.example.demo.document.controllerss;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.document.dto.DocumentDTO;
import com.example.demo.document.dto.DocumentDTOMapper;
import com.example.demo.document.entity.Document;
import com.example.demo.document.repository.DocumentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentDTOMapper documentDTOMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;  // Autowire RabbitTemplate for RabbitMQ messaging

    private final String queueName = "documentQueue";

    public DocumentController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @GetMapping
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Document getDocumentById(@PathVariable Long id) {
        return documentRepository.findById(id).orElse(null);
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentDTO> uploadDocument(
            @RequestParam("title") String title,
            @RequestParam("content") String content) {

        // Create Document entity using builder pattern
        Document document = Document.builder()
                .title(title)
                .content(content)
                .build();

        // Save the Document entity to the database
        Document savedDocument = documentRepository.save(document);

        // Convert the saved Document entity to DocumentDTO
        DocumentDTO documentDTO = documentDTOMapper.toDTO(savedDocument);

        // Send message to RabbitMQ queue
        try {
            String message = "Document uploaded: " + savedDocument.getTitle();
            rabbitTemplate.convertAndSend(queueName, message);
            logger.info("Message sent to RabbitMQ: {}", message);
        } catch (Exception e) {
            logger.error("Failed to send message to RabbitMQ: {}", e.getMessage());
        }

        // Return the DocumentDTO in the response
        return ResponseEntity.ok(documentDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteDocument(@PathVariable Long id) {
        try {
            documentRepository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
            documentRepository.deleteById(id);
            return "Document deleted successfully";
        } catch (Exception e) {
            logger.error("Error deleting document: {}", e.getMessage());
            return "Document not found";
        }
    }
}
