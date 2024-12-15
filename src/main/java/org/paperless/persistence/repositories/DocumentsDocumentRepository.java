package org.paperless.persistence.repositories;

import org.paperless.persistence.entities.DocumentsDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DocumentsDocumentRepository extends JpaRepository<DocumentsDocument, Integer> {
    List<DocumentsDocument> findByTitleOrderById(String title);
}
