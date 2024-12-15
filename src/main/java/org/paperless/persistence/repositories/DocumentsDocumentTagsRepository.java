package org.paperless.persistence.repositories;

import org.paperless.persistence.entities.DocumentsDocumentTags;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DocumentsDocumentTagsRepository extends JpaRepository<DocumentsDocumentTags, Integer> {
}
