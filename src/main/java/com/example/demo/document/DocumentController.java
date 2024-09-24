package com.example.demo.document;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping
    public Document createDocument(@RequestBody Document documents) {
        return documentRepository.save(documents);
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