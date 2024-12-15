package org.paperless;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.paperless.bl.mapper.DocumentNotesMapperImpl;
import org.paperless.model.GetDocuments200ResponseResultsInnerNotesInner;
import org.paperless.persistence.entities.AuthUser;
import org.paperless.persistence.entities.DocumentsDocument;
import org.paperless.persistence.entities.DocumentsNote;
import org.paperless.persistence.repositories.AuthUserRepository;
import org.paperless.persistence.repositories.DocumentsDocumentRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DocumentNotesMapperTest {

    @Mock
    private AuthUserRepository userRepository;

    @Mock
    private DocumentsDocumentRepository documentRepository;

    @InjectMocks
    private DocumentNotesMapperImpl mapper;

    @Test
    public void testDtoToEntity() {
        // Mock DTO
        GetDocuments200ResponseResultsInnerNotesInner dto = new GetDocuments200ResponseResultsInnerNotesInner();
        dto.setDocument(1);
        dto.setUser(2);
        dto.setNote("Note content");
        dto.setCreated("2024-02-21T12:00:00Z");

        // Mock behavior of repositories
        when(documentRepository.findById(1)).thenReturn(java.util.Optional.of(new DocumentsDocument()));
        when(userRepository.findById(2)).thenReturn(java.util.Optional.of(new AuthUser()));

        // Perform mapping
        DocumentsNote entity = mapper.dtoToEntity(dto);

        // Verify mapping
        assertEquals(dto.getNote(), entity.getNote());
    }

    @Test
    public void testEntityToDto() {
        // Mock entity
        DocumentsNote entity = new DocumentsNote();
        entity.setNote("Note content");
        entity.setDocument(new DocumentsDocument());
        entity.setUser(new AuthUser());
        entity.setCreated(java.time.OffsetDateTime.now());

        // Perform mapping
        GetDocuments200ResponseResultsInnerNotesInner dto = mapper.entityToDto(entity);

        // Verify mapping
        assertEquals(entity.getNote(), dto.getNote());
    }
}
