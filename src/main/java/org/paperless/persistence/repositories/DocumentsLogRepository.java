package org.paperless.persistence.repositories;

import org.paperless.persistence.entities.DocumentsLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DocumentsLogRepository extends JpaRepository<DocumentsLog, Integer> {
}
