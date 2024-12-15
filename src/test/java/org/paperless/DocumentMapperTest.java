package org.paperless;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.paperless.bl.mapper.DocumentMapperImpl;
import org.paperless.model.DocumentDTO;
import org.paperless.persistence.entities.DocumentsCorrespondent;
import org.paperless.persistence.entities.DocumentsDocument;
import org.paperless.persistence.entities.DocumentsDocumenttype;
import org.paperless.persistence.entities.DocumentsStoragepath;
import org.paperless.persistence.repositories.*;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DocumentMapperTest {

    @Mock
    private DocumentsCorrespondentRepository correspondentRepository;

    @Mock
    private DocumentsDocumenttypeRepository documentTypeRepository;

    @Mock
    private DocumentsStoragepathRepository storagePathRepository;

    @Mock
    private AuthUserRepository userRepository;

    @Mock
    private DocumentsDocumentTagsRepository documentTagsRepository;

    @InjectMocks
    private DocumentMapperImpl mapper;

    @Test
    public void testDtoToEntity() {
        // Mock DTO
        DocumentDTO dto = new DocumentDTO();
        dto.setId(1);
        dto.setCorrespondent(JsonNullable.of(2));
        dto.setDocumentType(JsonNullable.of(3));
        dto.setStoragePath(JsonNullable.of(4));
        dto.setTags(JsonNullable.of(List.of(1,2)));
        dto.setCreated(OffsetDateTime.now());

        // Mock behavior of repositories
        DocumentsCorrespondent documentsCorrespondent = new DocumentsCorrespondent();
        documentsCorrespondent.setId(2);
        when(correspondentRepository.findById(2)).thenReturn(java.util.Optional.of(documentsCorrespondent));
        when(documentTypeRepository.findById(3)).thenReturn(java.util.Optional.of(new DocumentsDocumenttype()));
        when(storagePathRepository.findById(4)).thenReturn(java.util.Optional.of(new DocumentsStoragepath()));

        // Perform mapping
        DocumentsDocument entity = mapper.dtoToEntity(dto);

        // Verify mapping
        assertEquals(dto.getId(), entity.getId());
        assertEquals(documentsCorrespondent.getId(), entity.getCorrespondent().getId());
    }

    @Test
    public void testEntityToDto() {
        // Mock entity
        DocumentsDocument entity = new DocumentsDocument();
        entity.setId(1);
        entity.setCorrespondent(new DocumentsCorrespondent());
        entity.setDocumentType(new DocumentsDocumenttype());
        entity.setStoragePath(new DocumentsStoragepath());
        entity.setCreated(OffsetDateTime.now());
        entity.setModified(OffsetDateTime.now());
        entity.setAdded(OffsetDateTime.now());

        // Perform mapping
        DocumentDTO dto = mapper.entityToDto(entity);

        // Verify mapping
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getCorrespondent().getId(), dto.getCorrespondent().get());
    }
}
