package org.paperless.bl.services;

import org.paperless.persistence.entities.DocumentsDocument;

import java.io.IOException;
import java.util.List;

public interface IESService {
    List<DocumentsDocument> searchEsForDocument(String searchTerm) throws IOException;
}
