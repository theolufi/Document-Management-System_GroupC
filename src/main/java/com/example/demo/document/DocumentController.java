package com.example.demo.document;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class DocumentController {

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }


    @GetMapping
    public List<Document> getAllDocuments() {
        return documentRepository.findAll(); // Fetch all documents from the repository
    }

    @GetMapping("/{id}")
    public Document getDocumentById(@PathVariable Long id) {
        Optional<Document> document = documentRepository.findById(id);
        return document.orElse(null); // Return null if not found; you might want to handle this case differently
    }

    @PostMapping
    public Document createDocument(@RequestBody Document document) {
        return documentRepository.save(document); // Save the new document to the repository
    }

    @DeleteMapping("/{id}")
    public String deleteDocument(@PathVariable Long id) {
        Optional<Document> document = documentRepository.findById(id);
        if (document.isPresent()) {
            documentRepository.deleteById(id);
            return "Document deleted successfully";
        } else {
            return "Document not found";
        }
    }
}
