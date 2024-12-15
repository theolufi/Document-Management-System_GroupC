package org.paperless.persistence.repositories;

import org.paperless.persistence.entities.DocumentsTag;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DocumentsTagRepository extends JpaRepository<DocumentsTag, Integer> {
}
