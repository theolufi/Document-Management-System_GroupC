package com.example.demo.document.controllerss;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.document.dto.DocumentDTO;
import com.example.demo.document.dto.DocumentDTOMapper;
import com.example.demo.document.entity.Document;
import com.example.demo.document.repository.DocumentRepository;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocumentDTOMapper documentDTOMapper;


    public DocumentController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @GetMapping
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Document getDocumentById(@PathVariable Long id) {
        return documentRepository.findById(id).get();
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

        // Return the DocumentDTO in the response
        return ResponseEntity.ok(documentDTO);
    }


    @DeleteMapping("/{id}")
    public String deleteDocument(@PathVariable Long id) {
        try {
        documentRepository.findById(id).get();
        documentRepository.deleteById(id);
        return "Document deleted successfully";
        } catch (Exception e) {
        return "Document not found";
        }
  }
}