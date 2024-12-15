package org.paperless.persistence.repositories;

import org.paperless.persistence.entities.DocumentsStoragepath;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DocumentsStoragepathRepository extends JpaRepository<DocumentsStoragepath, Integer> {
}
