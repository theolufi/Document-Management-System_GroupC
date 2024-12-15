package org.paperless;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.paperless.bl.mapper.*;
import org.paperless.model.GetDocument200Response;
import org.paperless.persistence.entities.*;
import org.paperless.persistence.repositories.*;

import java.time.OffsetDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
public class GetDocument200ResponseMapperTests {

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
    private GetDocument200ResponseMapperImpl mapper;

    @Test
    public void testDtoToEntity() {
        // Mock the DTO and entities
        GetDocument200Response dto = new GetDocument200Response();
        dto.setId(1);
        dto.setCorrespondent(2);
        dto.setDocumentType(3);
        dto.setStoragePath(4);
        dto.setTitle("Title");
        dto.setContent("Content");
        dto.setTags(Collections.singletonList(5));
        dto.setCreated("2012-01-01T00:00:00+00:00");
        dto.setModified("2012-01-01T00:00:00+00:00");
        dto.setAdded("2012-01-01T00:00:00+00:00");

        // Mock the behavior of the repositories
        when(correspondentRepository.findById(2)).thenReturn(java.util.Optional.of(new DocumentsCorrespondent()));
        when(documentTypeRepository.findById(3)).thenReturn(java.util.Optional.of(new DocumentsDocumenttype()));
        when(storagePathRepository.findById(4)).thenReturn(java.util.Optional.of(new DocumentsStoragepath()));

        // Call the method under test
        DocumentsDocument entity = mapper.dtoToEntity(dto);

        // Verify that the mapping was performed correctly
        verify(correspondentRepository).findById(2);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        assertNotNull(entity.getCreated());
    }

    @Test
    public void testEntityToDto() {
        // Mock the entity and DTO
        DocumentsDocument entity = new DocumentsDocument();
        entity.setId(1);
        entity.setCorrespondent(new DocumentsCorrespondent());
        entity.setDocumentType(new DocumentsDocumenttype());
        entity.setStoragePath(new DocumentsStoragepath());
        entity.setTitle("Title");
        entity.setContent("Content");
        entity.setCreated(OffsetDateTime.now());
        entity.setModified(OffsetDateTime.now());
        entity.setAdded(OffsetDateTime.now());

        // Mock the behavior of the repositories

        // Call the method under test
        GetDocument200Response dto = mapper.entityToDto(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getContent(), dto.getContent());
        assertNotNull(dto.getCreated());
    }

}
