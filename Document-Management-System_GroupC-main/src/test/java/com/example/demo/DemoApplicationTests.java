package com.example.demo;

import com.example.demo.document.dto.DocumentDTO;
import com.example.demo.document.dto.DocumentDTOMapper;
import com.example.demo.document.entity.Document;
import com.example.demo.document.repository.DocumentRepository;
import com.example.demo.document.controllerss.DocumentController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class) // Enable Mockito annotations
@SpringBootTest
@ActiveProfiles("test")
class DemoApplicationTests {

    @Mock
    private DocumentDTOMapper mapper; // Mock the DocumentDTOMapper

    @Mock
    private DocumentRepository documentRepository; // Mock the DocumentRepository

    @InjectMocks
    private DocumentController documentController; // Use InjectMocks to create the controller

    private MockMvc mockMvc; // Declare mockMvc

    private final Validator validator;

    public DemoApplicationTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        mockMvc = MockMvcBuilders.standaloneSetup(documentController).build(); // Create MockMvc
    }

    @Test
    void contextLoads() {
    }

    // Test for DocumentDTOMapper
    @Test
    void testToDTO_NullDocument() {
        // Arrange
        Document document = null;

        // Act
        DocumentDTO result = mapper.toDTO(document);

        // Assert
        assertNull(result);
    }

    @Test
    void testToDTO_ValidDocument() {
        // Arrange
        Document document = Document.builder()
                .id(1L)
                .title("Test Document")
                .content("This is a test content.")
                .fileData(new byte[]{1, 2, 3}) // Example file data
                .build();

        // Act
        DocumentDTO result = mapper.toDTO(document);

        // Assert
        assertNotNull(result);
        assertEquals(document.getId(), result.getId());
        assertEquals(document.getTitle(), result.getTitle());
        assertEquals(document.getContent(), result.getContent());
    }

    @Test
    void testToEntity_NullDTO() {
        // Arrange
        DocumentDTO documentDTO = null;

        // Act
        Document result = mapper.toEntity(documentDTO);

        // Assert
        assertNull(result);
    }

    @Test
    void testToEntity_ValidDTO() {
        // Arrange
        DocumentDTO documentDTO = DocumentDTO.builder()
                .id(1L)
                .title("Test Document")
                .content("This is a test content.")
                .build();

        // Act
        Document result = mapper.toEntity(documentDTO);

        // Assert
        assertNotNull(result);
        assertEquals(documentDTO.getId(), result.getId());
        assertEquals(documentDTO.getTitle(), result.getTitle());
        assertEquals(documentDTO.getContent(), result.getContent());
    }

    // Validation Tests for Document Entity
    @Test
    void testValidDocument() {
        // Arrange
        Document document = Document.builder()
                .title("Valid Title")
                .content("This content is valid.")
                .fileData(new byte[]{1, 2, 3})
                .build();

        // Act
        Set<ConstraintViolation<Document>> violations = validator.validate(document);

        // Assert
        assertTrue(violations.isEmpty(), "Document should be valid");
    }

    @Test
    void testTitleTooLong() {
        // Arrange
        Document document = Document.builder()
                .title("T".repeat(101)) // 101 characters long
                .content("This content is valid.")
                .fileData(new byte[]{1, 2, 3})
                .build();

        // Act
        Set<ConstraintViolation<Document>> violations = validator.validate(document);

        // Assert
        assertEquals(1, violations.size(), "Document should have a validation error");
        assertEquals("Title must be between 1 and 100 characters", 
                     violations.iterator().next().getMessage());
    }

    @Test
    void testTitleBlank() {
        // Arrange
        Document document = Document.builder()
                .title("") // Blank title
                .content("This content is valid.")
                .fileData(new byte[]{1, 2, 3})
                .build();

        // Act
        Set<ConstraintViolation<Document>> violations = validator.validate(document);

        // Assert
        assertEquals(1, violations.size(), "Document should have a validation error");
        assertEquals("Title must be between 1 and 100 characters", 
                     violations.iterator().next().getMessage());
    }

    @Test
    void testContentBlank() {
        // Arrange
        Document document = Document.builder()
                .title("Valid Title")
                .content("") // Blank content
                .fileData(new byte[]{1, 2, 3})
                .build();

        // Act
        Set<ConstraintViolation<Document>> violations = validator.validate(document);

        // Assert
        assertEquals(1, violations.size(), "Document should have a validation error");
        assertEquals("Content cannot be blank", 
                     violations.iterator().next().getMessage());
    }

    // Tests for DocumentController
    @Test
    void testGetAllDocuments() throws Exception {
        // Arrange
        Document doc1 = Document.builder().id(1L).title("Doc1").content("Content1").fileData(new byte[]{}).build();
        Document doc2 = Document.builder().id(2L).title("Doc2").content("Content2").fileData(new byte[]{}).build();
        List<Document> documents = Arrays.asList(doc1, doc2);
        
        when(documentRepository.findAll()).thenReturn(documents);

        // Act & Assert
        mockMvc.perform(get("/api/documents")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetDocumentById() throws Exception {
        // Arrange
        Long docId = 1L;
        Document document = Document.builder().id(docId).title("Doc1").content("Content1").fileData(new byte[]{}).build();
        
        when(documentRepository.findById(docId)).thenReturn(Optional.of(document));

        // Act & Assert
        mockMvc.perform(get("/api/documents/{id}", docId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(docId))
                .andExpect(jsonPath("$.title").value("Doc1"));
    }

@Test
void testUploadDocument() throws Exception {
    // Arrange
    DocumentDTO documentDTO = DocumentDTO.builder()
            .id(1L)
            .title("Uploaded Doc")
            .content("Uploaded Content")
            .build();
    
    Document document = Document.builder()
            .id(1L)
            .title("Uploaded Doc")
            .content("Uploaded Content")
            .build();
    
    // Use specific argument matchers for the methods that require specific types
    when(mapper.toDTO(any(Document.class))).thenReturn(documentDTO); // Use the mapper instance
    when(documentRepository.save(any(Document.class))).thenReturn(document);
    
    // Act & Assert
    mockMvc.perform(post("/api/documents/upload")
            .param("title", "Uploaded Doc")
            .param("content", "Uploaded Content")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Uploaded Doc"));
}

    
    @Test
    void testDeleteDocument_NotFound() throws Exception {
        // Arrange
        Long docId = 1L;

        when(documentRepository.findById(docId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/documents/{id}", docId))
                .andExpect(status().isOk())
                .andExpect(content().string("Document not found"));
        
        verify(documentRepository, never()).deleteById(docId);
    }
}
