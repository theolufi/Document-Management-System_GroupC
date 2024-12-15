package org.paperless.bl.mapper;

import org.mapstruct.*;
import org.paperless.model.GetDocuments200ResponseResultsInnerNotesInner;
import org.paperless.persistence.entities.AuthUser;
import org.paperless.persistence.entities.DocumentsDocument;
import org.paperless.persistence.entities.DocumentsNote;
import org.paperless.persistence.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class DocumentNotesMapper implements AbstractMapper<DocumentsNote, GetDocuments200ResponseResultsInnerNotesInner> {

    @Autowired
    private AuthUserRepository userRepository;
    @Autowired
    private DocumentsDocumentRepository documentRepository;

    @Mapping(target = "document", source = "document", qualifiedByName = "documentDto")
    @Mapping(target = "user", source = "user", qualifiedByName = "userDto")
    abstract public DocumentsNote dtoToEntity(GetDocuments200ResponseResultsInnerNotesInner dto);

    @Mapping(target = "document", source = "document", qualifiedByName = "documentEntity")
    @Mapping(target = "user", source = "user", qualifiedByName = "userEntity")
    abstract public GetDocuments200ResponseResultsInnerNotesInner entityToDto(DocumentsNote entity);

    @Named("userEntity")
    Integer map(AuthUser user) {
        return user.getId();
    }

    @Named("documentEntity")
    Integer map(DocumentsDocument document) {
        return document.getId();
    }

    @Named("createdEntity")
    String map(OffsetDateTime created) {
        return created.toString();
    }

    @Named("userDto")
    AuthUser mapCorrespondent(Integer value) {
        return userRepository.findById(value).orElse(null);
    }

    @Named("documentDto")
    DocumentsDocument mapDocument(Integer value) {
        return documentRepository.findById(value).orElse(null);
    }


}
