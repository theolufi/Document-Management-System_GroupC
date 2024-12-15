package org.paperless.persistence.repositories;

import org.paperless.persistence.entities.DocumentsNote;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DocumentsNoteRepository extends JpaRepository<DocumentsNote, Integer> {
}
